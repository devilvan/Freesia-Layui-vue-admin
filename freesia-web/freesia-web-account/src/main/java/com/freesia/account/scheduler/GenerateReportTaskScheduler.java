package com.freesia.account.scheduler;

import cn.hutool.core.util.RandomUtil;
import com.freesia.account.AccountReportSchedulerHelper;
import com.freesia.account.dto.AccountBudgetDto;
import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.dto.AccountReportDto;
import com.freesia.account.entity.FindPageAccountCostEntity;
import com.freesia.account.service.AccountBudgetService;
import com.freesia.account.service.AccountCostService;
import com.freesia.account.service.AccountReportService;
import com.freesia.constant.BudgetType;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.Constants;
import com.freesia.dto.SysTenantDto;
import com.freesia.dto.SysUserDto;
import com.freesia.po.SysUserPo;
import com.freesia.redis.util.URedis;
import com.freesia.repository.SysUserRepository;
import com.freesia.service.SysTenantService;
import com.freesia.util.UEmpty;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Bliss.Wu
 * @Description 生成报表任务 定时器
 * @date 2026-02-26
 */
@Component
@RequiredArgsConstructor
public class GenerateReportTaskScheduler {
    private final AccountBudgetService accountBudgetService;
    private final AccountCostService accountCostService;
    private final AccountReportService accountReportService;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final SysUserRepository sysUserRepository;
    private final SysTenantService sysTenantService;

    @XxlJob("generateReportTask")
    public void generateReportTask() {
        List<SysUserPo> sysUserPoList = sysUserRepository.findAll();
        List<SysUserDto> sysUserDtoList = new ArrayList<>();
        for (SysUserPo sysUserPo : sysUserPoList) {
            Long userId = sysUserPo.getId();
            List<SysTenantDto> sysTenantDtoList = sysTenantService.findListSysTenantByUserId(userId);
            if (UEmpty.isEmpty(sysTenantDtoList)) {
                continue;
            }
            for (SysTenantDto sysTenantDto : sysTenantDtoList) {
                Long tenantId = sysTenantDto.getId();
                SysUserDto sysUserDto = new SysUserDto();
                sysUserDto.setId(userId);
                sysUserDto.setTenantId(tenantId);
                sysUserDtoList.add(sysUserDto);
            }
        }
        if (UEmpty.isNotEmpty(sysUserDtoList)) {
            generateReportTask(sysUserDtoList);
        }
    }

    public void generateReportTask(List<SysUserDto> sysUserDtoList) {
        for (SysUserDto sysUserDto : sysUserDtoList) {
            if (sysUserDto.getId() == null || sysUserDto.getTenantId() == null) {
                continue;
            }
            threadPoolTaskExecutor.execute(() -> {
                generateReport(sysUserDto);
            });
        }
    }

    private void generateReport(SysUserDto sysUserDto) {
        Long userId = sysUserDto.getId();
        Long tenantId = sysUserDto.getTenantId();
        // 查询用户设置的预算
        AccountBudgetDto accountBudgetDto = new AccountBudgetDto();
        accountBudgetDto.setUserId(userId);
        accountBudgetDto.setTenantId(tenantId);
        List<AccountBudgetDto> accountBudgetDtoList = accountBudgetService.findList(accountBudgetDto);
        if (UEmpty.isNotEmpty(accountBudgetDtoList)) {
            // 查询缓存本账单最早的记账时间，无则查询并加入缓存
            String minPaymentTimeFormat = buildMinPaymentTimeFormat(tenantId, userId);
            if (UEmpty.isNotEmpty(minPaymentTimeFormat)) {
                // 解析最早报表时间
                Date minDate = parseMinDate(minPaymentTimeFormat);
                // 遍历预算，根据预算类型生成账单任务
                for (AccountBudgetDto dto : accountBudgetDtoList) {
                    generatePeriodBilling(dto, minDate, new Date(), userId, tenantId);
                }
            }
        }
    }

    /**
     * 解析最早报表时间
     *
     * @param minPaymentTimeFormat 最早报表时间（字符串）
     * @return 最早报表时间（日期）
     */
    private Date parseMinDate(String minPaymentTimeFormat) {
        Date earliestDate;
        try {
            earliestDate = new SimpleDateFormat(Constants.YMD).parse(minPaymentTimeFormat);
        } catch (ParseException e) {
            earliestDate = new Date();
        }
        return earliestDate;
    }

    /**
     * 查询缓存本账单最早的记账时间，无则查询并加入缓存
     * 格式{@link Constants#SDF_YMD}
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 最早记账时间
     */
    private String buildMinPaymentTimeFormat(Long tenantId, Long userId) {
        String cacheKey = CacheConstant.FIND_CACHE_ACCOUNT_COST_EARLY_PAYMENT_TIME + userId + "@" + tenantId;
        String minPaymentTimeFormat = URedis.get(cacheKey);
        if (UEmpty.isEmpty(minPaymentTimeFormat)) {
            AccountCostDto accountCostDto = new AccountCostDto();
            accountCostDto.setUserId(userId);
            accountCostDto.setTenantId(tenantId);
            Date minPaymentTime = accountCostService.findMinPaymentTime(accountCostDto);
            if (minPaymentTime != null) {
                minPaymentTimeFormat = Constants.SDF_YMD.format(minPaymentTime);
                // 设置缓存键的随机分钟数
                String durationFormat = "P1DT" + RandomUtil.randomInt(2, 11) + "M";
                URedis.set(cacheKey, minPaymentTimeFormat, Duration.parse(durationFormat));
            } else {
                minPaymentTimeFormat = "";
            }
        }
        return minPaymentTimeFormat;
    }

    /**
     * 根据预算类型生成周期账单
     *
     * @param dto          预算DTO
     * @param earliestDate 最早记账时间
     * @param today        今天
     * @param userId       用户ID
     * @param tenantId     租户ID
     */
    private void generatePeriodBilling(AccountBudgetDto dto, Date earliestDate, Date today, Long userId, Long tenantId) {
        String budgetType = dto.getBudgetType();
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(today);

        // 根据预算类型计算起始周期
        if (BudgetType.DAY.getCode().equals(budgetType)) {
            startCal.setTime(earliestDate);
        } else if (BudgetType.WEEK.getCode().equals(budgetType)) {
            startCal.setTime(earliestDate);
            startCal.add(Calendar.DAY_OF_MONTH, -(startCal.get(Calendar.DAY_OF_WEEK) - 2) % 7);
        } else if (BudgetType.MONTH.getCode().equals(budgetType)) {
            startCal.setTime(earliestDate);
            startCal.set(Calendar.DAY_OF_MONTH, 1);
        } else if (BudgetType.YEAR.getCode().equals(budgetType)) {
            startCal.setTime(earliestDate);
            startCal.set(Calendar.DAY_OF_YEAR, 1);
        } else {
            return;
        }

        List<AccountReportDto> accountReportDtoList = new ArrayList<>();
        // 遍历生成每个周期的账单
        while (!startCal.after(endCal)) {
            Date billingTimeFrom = getBillingTimeFrom(startCal.getTime(), budgetType);
            Date billingTimeTo = getBillingTimeTo(startCal.getTime(), budgetType);

            // 检查账单是否已存在
            Boolean flag = findExistReport(dto, billingTimeFrom);
            if (flag) {
                moveToNextPeriod(startCal, budgetType);
                continue;
            }
            AccountReportDto reportDto = new AccountReportDto();
            reportDto.setUserId(userId);
            reportDto.setTenantId(tenantId);
            reportDto.setBudgetId(dto.getId());
            reportDto.setStrategyId(dto.getStrategyId());
            reportDto.setTitle(dto.getBudgetDesc());
            reportDto.setBudgetType(budgetType);
            reportDto.setBillingTime(billingTimeTo);
            reportDto.setBillingTimeFrom(billingTimeFrom);
            reportDto.setBillingTimeTo(billingTimeTo);
            reportDto.setRecalculateFlag(true);
            // 查询期间收支记录
            List<FindPageAccountCostEntity> findAccountCostEntityList = AccountReportSchedulerHelper.findListAccountCost(accountCostService, userId, tenantId, billingTimeFrom, billingTimeTo);
            // 构建收支金额
            AccountReportSchedulerHelper.buildReportOutlayIncome(userId, findAccountCostEntityList, reportDto);
            accountReportDtoList.add(reportDto);
            // 移动到下一个周期
            moveToNextPeriod(startCal, budgetType);
        }
        if (UEmpty.isNotEmpty(accountReportDtoList)) {
            accountReportService.saveUpdateBatch(accountReportDtoList);
        }
    }

    /**
     * 查询账单数据是否存在
     *
     * @param dto             预算实体
     * @param billingTimeFrom 报表时间从
     * @return 是否存在
     */
    private Boolean findExistReport(AccountBudgetDto dto, Date billingTimeFrom) {
        AccountReportDto accountReportDto = new AccountReportDto();
        accountReportDto.setBudgetId(dto.getId());
        accountReportDto.setBudgetType(dto.getBudgetType());
        accountReportDto.setBillingTimeFrom(billingTimeFrom);
        return accountReportService.findExist(accountReportDto);
    }


    /**
     * 获取账单周期开始时间
     *
     * @param date       报表时间从
     * @param budgetType 预算类型
     * @return 账单周期开始时间
     */
    private Date getBillingTimeFrom(Date date, String budgetType) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (BudgetType.WEEK.getCode().equals(budgetType)) {
            cal.add(Calendar.DAY_OF_MONTH, -(cal.get(Calendar.DAY_OF_WEEK) - 2) % 7);
        } else if (BudgetType.MONTH.getCode().equals(budgetType)) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
        } else if (BudgetType.YEAR.getCode().equals(budgetType)) {
            cal.set(Calendar.DAY_OF_YEAR, 1);
        }
        return cal.getTime();
    }

    /**
     * 获取账单周期结束时间
     *
     * @param date       报表时间从
     * @param budgetType 预算类型
     * @return 账单周期开始时间
     */
    private Date getBillingTimeTo(Date date, String budgetType) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        if (BudgetType.DAY.getCode().equals(budgetType)) {
            return cal.getTime();
        } else if (BudgetType.WEEK.getCode().equals(budgetType)) {
            cal.add(Calendar.DAY_OF_MONTH, 6 - (cal.get(Calendar.DAY_OF_WEEK) - 2) % 7);
        } else if (BudgetType.MONTH.getCode().equals(budgetType)) {
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (BudgetType.YEAR.getCode().equals(budgetType)) {
            cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
        }
        return cal.getTime();
    }

    /**
     * 移动到下一个周期
     */
    private void moveToNextPeriod(Calendar cal, String budgetType) {
        if (BudgetType.DAY.getCode().equals(budgetType)) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        } else if (BudgetType.WEEK.getCode().equals(budgetType)) {
            cal.add(Calendar.DAY_OF_MONTH, 7);
        } else if (BudgetType.MONTH.getCode().equals(budgetType)) {
            cal.add(Calendar.MONTH, 1);
        } else if (BudgetType.YEAR.getCode().equals(budgetType)) {
            cal.add(Calendar.YEAR, 1);
        }
    }
}
