package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freesia.account.converter.AccountReportConverter;
import com.freesia.account.dto.AccountBudgetDto;
import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.dto.AccountReportDto;
import com.freesia.account.mapper.AccountReportMapper;
import com.freesia.account.po.AccountReportPo;
import com.freesia.account.repository.AccountReportRepository;
import com.freesia.account.service.AccountBudgetService;
import com.freesia.account.service.AccountCostService;
import com.freesia.account.service.AccountReportService;
import com.freesia.account.vo.AccountReportVo;
import com.freesia.constant.BudgetType;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.Constants;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysUserDto;
import com.freesia.redis.util.URedis;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import com.freesia.util.UStream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 记账报表表 业务逻辑类
 * @date 2026-02-25
 */
@Service
@RequiredArgsConstructor
public class AccountReportServiceImpl extends BaseServiceImpl<AccountReportMapper, AccountReportVo, AccountReportDto, AccountReportPo> implements AccountReportService {
    private final AccountReportRepository accountReportRepository;
    private final AccountReportConverter accountReportConverter;
    private final AccountBudgetService accountBudgetService;
    private final AccountCostService accountCostService;

    @Override
    protected MapStructConverter<AccountReportVo, AccountReportDto, AccountReportPo> getMapStructConverter() {
        return accountReportConverter;
    }

    @Override
    protected JpaRepository<AccountReportPo, Long> getRepository() {
        return accountReportRepository;
    }

    @Override
    protected Class<AccountReportDto> getDtoClass() {
        return AccountReportDto.class;
    }

    @Override
    protected Class<AccountReportPo> getPoClass() {
        return AccountReportPo.class;
    }

    @Override
    protected Wrapper<AccountReportPo> buildQueryWrapper(@NonNull AccountReportDto accountReportDto) {
        return new LambdaQueryWrapper<AccountReportPo>()
                .eq(AccountReportPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountReportDto.getId()), AccountReportPo::getId, accountReportDto.getId())
                .eq(UEmpty.isNotEmpty(accountReportDto.getRemark()), AccountReportPo::getRemark, accountReportDto.getRemark())
                .eq(UEmpty.isNotEmpty(accountReportDto.getUserId()), AccountReportPo::getUserId, accountReportDto.getUserId())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBudgetId()), AccountReportPo::getBudgetId, accountReportDto.getBudgetId())
                .eq(UEmpty.isNotEmpty(accountReportDto.getStrategyId()), AccountReportPo::getStrategyId, accountReportDto.getStrategyId())
                .eq(UEmpty.isNotEmpty(accountReportDto.getTitle()), AccountReportPo::getTitle, accountReportDto.getTitle())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBudgetType()), AccountReportPo::getBudgetType, accountReportDto.getBudgetType())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBudgetAmount()), AccountReportPo::getBudgetAmount, accountReportDto.getBudgetAmount())
                .eq(UEmpty.isNotEmpty(accountReportDto.getOutlay()), AccountReportPo::getOutlay, accountReportDto.getOutlay())
                .eq(UEmpty.isNotEmpty(accountReportDto.getIncomeAmount()), AccountReportPo::getIncomeAmount, accountReportDto.getIncomeAmount())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBillingTime()), AccountReportPo::getBillingTime, accountReportDto.getBillingTime())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBillingTimeFrom()), AccountReportPo::getBillingTimeFrom, accountReportDto.getBillingTimeFrom())
                .eq(UEmpty.isNotEmpty(accountReportDto.getBillingTimeTo()), AccountReportPo::getBillingTimeTo, accountReportDto.getBillingTimeTo())
                .eq(UEmpty.isNotEmpty(accountReportDto.getRecalculateFlag()), AccountReportPo::getRecalculateFlag, accountReportDto.getRecalculateFlag())
                ;
    }

    @Override
    public void generateReportTask(SysUserDto sysUserDto) {
        Long userId = sysUserDto.getId();
        // 查询用户设置的预算
        AccountBudgetDto accountBudgetDto = new AccountBudgetDto();
        accountBudgetDto.setUserId(userId);
        accountBudgetDto.setTenantId(sysUserDto.getTenantId());
        List<AccountBudgetDto> accountBudgetDtoList = accountBudgetService.findList(accountBudgetDto);
        if (UEmpty.isNotEmpty(accountBudgetDtoList)) {
            // 根据租户ID分组
            Map<Long, List<AccountBudgetDto>> groupingByTenantIdMapList = UStream.groupingByKey(accountBudgetDtoList, AccountBudgetDto::getTenantId);
            if (UEmpty.isNotEmpty(groupingByTenantIdMapList)) {
                // 遍历每个租户的预算，判断是否需要生成账单
                Set<Map.Entry<Long, List<AccountBudgetDto>>> entrySet = groupingByTenantIdMapList.entrySet();
                for (Map.Entry<Long, List<AccountBudgetDto>> entry : entrySet) {
                    Long tenantId = entry.getKey();
                    List<AccountBudgetDto> budgetDtoList = entry.getValue();
                    // 查询缓存本账单最早的记账时间，无则查询并加入缓存
                    String minPaymentTimeFormat = buildMinPaymentTimeFormat(tenantId, userId);
                    if (UEmpty.isNotEmpty(minPaymentTimeFormat)) {
                        Date earliestDate;
                        try {
                            earliestDate = Constants.SDF_YMD.parse(minPaymentTimeFormat);
                        } catch (ParseException e) {
                            earliestDate = new Date();
                        }
                        // 遍历预算，根据预算类型生成账单任务
                        for (AccountBudgetDto dto : budgetDtoList) {
                            generatePeriodBilling(dto, earliestDate, new Date(), userId);
                        }
                    }
                }
            }
            // 遍历预算，根据预算天数生成账单数据
        }
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
        String minPaymentTimeFormat = URedis.get(CacheConstant.FIND_CACHE_ACCOUNT_COST_EARLY_PAYMENT_TIME + tenantId);
        if (UEmpty.isEmpty(minPaymentTimeFormat)) {
            AccountCostDto accountCostDto = new AccountCostDto();
            accountCostDto.setUserId(userId);
            accountCostDto.setTenantId(tenantId);
            Date minPaymentTime = accountCostService.findMinPaymentTime(accountCostDto);
            if (minPaymentTime != null) {
                minPaymentTimeFormat = Constants.SDF_YMD.format(minPaymentTime);
                URedis.set(CacheConstant.FIND_CACHE_ACCOUNT_COST_EARLY_PAYMENT_TIME + tenantId, minPaymentTimeFormat);
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
     */
    private void generatePeriodBilling(AccountBudgetDto dto, Date earliestDate, Date today, Long userId) {
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

        List<AccountReportPo> accountReportPoList = new ArrayList<>();
        // 遍历生成每个周期的账单
        while (!startCal.after(endCal)) {
            Date billingTimeFrom = getBillingTimeFrom(startCal.getTime(), budgetType);
            Date billingTimeTo = getBillingTimeTo(startCal.getTime(), budgetType);

            // 检查账单是否已存在
            List<AccountReportPo> existReports = accountReportRepository
                    .findByBudgetIdAndBudgetTypeAndBillingTimeFrom(dto.getId(), budgetType, billingTimeFrom);
            if (UEmpty.isNotEmpty(existReports)) {
                moveToNextPeriod(startCal, budgetType);
                continue;
            }

            AccountReportPo reportPo = new AccountReportPo();
            reportPo.setUserId(userId);
            reportPo.setBudgetId(dto.getId());
            reportPo.setStrategyId(dto.getStrategyId());
            reportPo.setTitle(dto.getBudgetDesc());
            reportPo.setBudgetType(budgetType);
            reportPo.setBudgetAmount(dto.getOutlay());
            reportPo.setBillingTime(startCal.getTime());
            reportPo.setBillingTimeFrom(billingTimeFrom);
            reportPo.setBillingTimeTo(billingTimeTo);
            reportPo.setRecalculateFlag(true);
            accountReportPoList.add(reportPo);
            // 移动到下一个周期
            moveToNextPeriod(startCal, budgetType);
        }
        if (UEmpty.isNotEmpty(accountReportPoList)) {
            accountReportRepository.saveAll(accountReportPoList);
        }
    }

    /**
     * 获取账单周期开始时间
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
     */
    private Date getBillingTimeTo(Date date, String budgetType) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);

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
