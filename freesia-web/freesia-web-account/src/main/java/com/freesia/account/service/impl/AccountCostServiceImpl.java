package com.freesia.account.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.account.constant.CostType;
import com.freesia.account.constant.DateScope;
import com.freesia.account.converter.AccountCostConverter;
import com.freesia.account.dto.*;
import com.freesia.account.entity.*;
import com.freesia.account.exception.AccountException;
import com.freesia.account.mapper.AccountCostMapper;
import com.freesia.account.po.AccountCostPo;
import com.freesia.account.repository.AccountCostRepository;
import com.freesia.account.service.AccountBudgetService;
import com.freesia.account.service.AccountCostService;
import com.freesia.account.service.AccountCostUserAllocService;
import com.freesia.account.service.AccountReportService;
import com.freesia.account.vo.AccountCostVo;
import com.freesia.constant.BudgetType;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.Constants;
import com.freesia.convert.MapStructConverter;
import com.freesia.dto.BaseDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.EchartCalendarOptionEntity;
import com.freesia.entity.EchartLineOptionEntity;
import com.freesia.entity.EchartPieOptionEntity;
import com.freesia.entity.EchartStackedHorizontalBarOptionEntity;
import com.freesia.icon.dto.FindListSelectCostTypeDto;
import com.freesia.icon.service.CommonIconTemplateHeaderService;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.notice.constant.SysNoticeCategory;
import com.freesia.notice.constant.SysNoticeType;
import com.freesia.notice.util.UNotice;
import com.freesia.oss.pojo.OssFactory;
import com.freesia.oss.pojo.OssHandler;
import com.freesia.pojo.LaySelect;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.redis.util.URedis;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.SysUserService;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.sse.constant.SseTopic;
import com.freesia.sse.dto.SseMessageDto;
import com.freesia.sse.util.USse;
import com.freesia.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 开销表 业务逻辑类
 * @date 2024-12-14
 */
@Service
@RequiredArgsConstructor
public class AccountCostServiceImpl extends BaseServiceImpl<AccountCostMapper, AccountCostVo, AccountCostDto, AccountCostPo> implements AccountCostService {
    private final AccountCostRepository accountCostRepository;
    private final AccountCostMapper accountCostMapper;
    private final TransactionTemplate transactionTemplate;
    private final CommonIconTemplateHeaderService commonIconTemplateHeaderService;
    private final SysUserService sysUserService;
    private final AccountCostUserAllocService accountCostUserAllocService;
    private final AccountCostConverter accountCostConverter;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final AccountReportService accountReportService;
    private final AccountBudgetService accountBudgetService;


    @Override
    protected MapStructConverter<AccountCostVo, AccountCostDto, AccountCostPo> getMapStructConverter() {
        return accountCostConverter;
    }

    @Override
    protected JpaRepository<AccountCostPo, Long> getRepository() {
        return accountCostRepository;
    }

    @Override
    protected Class<AccountCostDto> getDtoClass() {
        return AccountCostDto.class;
    }

    @Override
    protected Class<AccountCostPo> getPoClass() {
        return AccountCostPo.class;
    }

    @Override
    protected Wrapper<AccountCostPo> buildQueryWrapper(@NonNull AccountCostDto dto) {
        return null;
    }


    @Override
    public AccountCostDto saveUpdate(AccountCostDto accountCostDto) {
        Long userId = USecurity.getUserId();
        Long tenantId = USecurity.getTenantId();
        SysUserDto sysUserDto = sysUserService.findUserById(userId);
        Long costId = accountCostDto.getId();
        OssHandler ossHandler = OssFactory.getInstance();
        accountCostDto.setIcon(ossHandler.convertDomain2Endpoint(accountCostDto.getIcon()));
        AccountCostPo accountCostPo = accountCostConverter.convertDto2Po(accountCostDto);
        List<Long> accountCostUserIdList = accountCostDto.getAccountCostUserIdList();
        if (UEmpty.isNull(costId)) {
            // 新增
            AccountCostPo afterInsertAccountCostPo = handleInsert(accountCostDto, accountCostPo, accountCostUserIdList, userId, sysUserDto);
            return accountCostConverter.convertPo2Dto(afterInsertAccountCostPo);
        } else {
            // 修改
            executeChangeReportRecalculateFlag(accountCostDto, userId, tenantId);
            AccountCostPo afterInsertAccountCostPo = handleUpdate(accountCostDto, accountCostPo, costId, accountCostUserIdList, userId, sysUserDto);
            return accountCostConverter.convertPo2Dto(afterInsertAccountCostPo);
        }
    }

    /**
     * 修改时线程执行修改报表重算标识
     *
     * @param accountCostDto 待更新的记账对象
     * @param userId         用户ID
     * @param tenantId       租户ID
     */
    private void executeChangeReportRecalculateFlag(AccountCostDto accountCostDto, Long userId, Long tenantId) {
        AccountCostPo originAccountCostPo = accountCostRepository.findById(accountCostDto.getId()).orElse(null);
        threadPoolTaskExecutor.execute(() -> {
            if (originAccountCostPo != null) {
                // 20260302-Bliss 修改金额或收支类型时，标记已生成的预算账单的重算标识为false
                if (!(accountCostDto.getPaymentSign().equals(originAccountCostPo.getPaymentSign()) && accountCostDto.getOutlay().compareTo(originAccountCostPo.getOutlay()) == 0)) {
                    // 查询预算
                    String findBudget = CacheConstant.FIND_BUDGET + userId;
                    List<AccountBudgetDto> accountBudgetDtoList = URedis.get(findBudget);
                    if (UEmpty.isEmpty(accountBudgetDtoList)) {
                        // 如果没有缓存预算则尝试缓存一次
                        accountBudgetService.cacheBudget(userId);
                        return;
                    }
                    Set<Long> reportIdSet = new HashSet<>();
                    for (AccountBudgetDto accountBudgetDto : accountBudgetDtoList) {
                        BudgetType budgetType = BudgetType.getInstanceByCode(accountBudgetDto.getBudgetType());
                        if (budgetType == null) {
                            continue;
                        }
                        AccountReportDto accountReportDto = new AccountReportDto();
                        accountReportDto.setUserId(userId);
                        accountReportDto.setTenantId(tenantId);
                        accountReportDto.setBudgetType(accountBudgetDto.getBudgetType());
                        accountReportDto.setBillingTime(accountCostDto.getPaymentTime());
                        List<AccountReportDto> accountReportDtoList = accountReportService.findBetweenBillingTime(accountReportDto);
                        if (UEmpty.isEmpty(accountReportDtoList)) {
                            continue;
                        }
                        reportIdSet.addAll(accountReportDtoList.stream().map(BaseDto::getId).collect(Collectors.toSet()));
                    }
                    if (UEmpty.isNotEmpty(reportIdSet)) {
                        accountReportService.changeRecalculateFlag(reportIdSet);
                    }
                }
            }
        });
    }

    @Override
    public TableResult<FindPageAccountCostEntity> findPageAccountCost(AccountCostDto accountCost, PageQuery pageQuery) {
        Page<Long> idPage = accountCostMapper.findPageAccountCostId(accountCost, pageQuery.build());
        if (idPage != null && UEmpty.isNotEmpty(idPage.getRecords())) {
            AccountCostDto accountCostDto = new AccountCostDto();
            accountCostDto.setUserId(accountCost.getUserId());
            accountCostDto.setTenantId(accountCost.getTenantId());
            accountCostDto.setIdList(idPage.getRecords());
            List<FindPageAccountCostEntity> findPageAccountCostEntityList = accountCostMapper.findPageAccountCost(accountCostDto);
            Page<FindPageAccountCostEntity> pagePo = new Page<>();
            pagePo.setRecords(findPageAccountCostEntityList);
            pagePo.setSize(idPage.getSize());
            pagePo.setCurrent(idPage.getCurrent());
            pagePo.setTotal(idPage.getTotal());
            pagePo.setPages(idPage.getPages());
            return TableResult.build(pagePo);
        }
        return TableResult.build(new Page<>());
    }

    @Override
    public FindAccountCostEntity findAccountCost(AccountCostDto accountCost) {
        return accountCostMapper.findAccountCost(accountCost);
    }

    @Override
    public List<FindPageAccountCostEntity> findListAccountCost(AccountCostDto accountCost) {
        List<Long> idList = accountCostMapper.findListAccountCostId(accountCost);
        List<FindPageAccountCostEntity> findPageAccountCostEntityList = new ArrayList<>();
        if (UEmpty.isNotEmpty(idList)) {
            AccountCostDto accountCostDto = new AccountCostDto();
            accountCostDto.setUserId(accountCost.getUserId());
            accountCostDto.setTenantId(accountCost.getTenantId());
            accountCostDto.setIdList(idList);
            findPageAccountCostEntityList = accountCostMapper.findListAccountCost(accountCostDto);
            return findPageAccountCostEntityList;
        }
        return findPageAccountCostEntityList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccountCost(List<Long> idList) {
        List<AccountCostPo> accountCostPoList = accountCostRepository.findAllById(idList);
        List<Long> costIdList = UCollection.optimizeInitialCapacityArrayList(accountCostPoList.size());
        for (AccountCostPo accountCostPo : accountCostPoList) {
            costIdList.add(accountCostPo.getId());
        }
        accountCostUserAllocService.deleteAccountCostUserAllocByCostId(costIdList);
        accountCostRepository.deleteAllByIdInBatch(idList);
    }

    @Override
    public List<AccountCostExportEntity> findBuildListAccountsExport(AccountCostDto accountCostDto) {
        List<AccountCostExportEntity> findListAccountsExport = accountCostMapper.findListAccountsExport(accountCostDto);
        Map<String, List<AccountCostExportEntity>> dateListMap = UStream.groupingByKey(findListAccountsExport, AccountCostExportEntity::getPaymentTimeGroupingKey);
        List<AccountCostExportEntity> toExportList = new ArrayList<>();
        if (UEmpty.isNotEmpty(dateListMap)) {
            Set<Map.Entry<String, List<AccountCostExportEntity>>> entrySet = dateListMap.entrySet();
            for (Map.Entry<String, List<AccountCostExportEntity>> entry : entrySet) {
                List<AccountCostExportEntity> accountCostExportEntityList = entry.getValue();
                // 每个分组最后一行添加合计列
                buildStatisticRow(accountCostExportEntityList);
                toExportList.addAll(accountCostExportEntityList);
            }
        }
        toExportList.sort(Comparator.comparing(AccountCostExportEntity::getPaymentTime));
        return toExportList;
    }

    @Override
    public EchartPieOptionEntity findCostTypeRatePie(AccountCostDto accountCostDto) {
        String cacheKey = "findCostTypeRatePie:" + accountCostDto.getUserId() + "@" + accountCostDto.getTenantId() + "@" + Constants.SDF_YMDHMS.format(accountCostDto.getPaymentTimeFrom()) + "@" + Constants.SDF_YMDHMS.format(accountCostDto.getPaymentTimeTo());
        EchartPieOptionEntity echartPieOptionEntityCache = URedis.get(cacheKey);
        if (UEmpty.isNotNull(echartPieOptionEntityCache)) {
            return echartPieOptionEntityCache;
        }
        List<FindCostTypeRatePieEntity> accountCostPoList = accountCostMapper.findCostTypeRatePie(accountCostDto);
        EchartPieOptionEntity echartPieOptionEntity = new EchartPieOptionEntity();
        if (UEmpty.isNotEmpty(accountCostPoList)) {
            BigDecimal sumOutlay = accountCostPoList.stream().map(FindCostTypeRatePieEntity::getOutlay).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            Set<String> legendSet = accountCostPoList.stream().map(FindCostTypeRatePieEntity::getCostType).collect(Collectors.toSet());
            echartPieOptionEntity.setLegends(legendSet);
            List<EchartPieOptionEntity.Series> series = new ArrayList<>();
            for (FindCostTypeRatePieEntity findCostTypeRatePieEntity : accountCostPoList) {
                EchartPieOptionEntity.Series tmp = new EchartPieOptionEntity.Series();
                tmp.setName(findCostTypeRatePieEntity.getCostType());
                BigDecimal outlay = findCostTypeRatePieEntity.getOutlay();
                if (UEmpty.isNotNull(outlay)) {
                    tmp.setValue(outlay.setScale(2, RoundingMode.HALF_UP).toString());
                }
                series.add(tmp);
            }
            echartPieOptionEntity.setSeries(series);
            echartPieOptionEntity.setTotalAmount(sumOutlay);
            URedis.set(cacheKey, echartPieOptionEntity, Duration.ofSeconds(30));
            return echartPieOptionEntity;
        }
        return null;
    }

    @Override
    public EchartLineOptionEntity findCostLineChart(FindCostLineChartDto findCostLineChartDto) {
        String dateScope = findCostLineChartDto.getDateScope();
        String formatPaymentTimeFrom;
        String formatPaymentTimeTo = null;
        if (DateScope.WEEK.getCode().equals(dateScope)) {
            formatPaymentTimeFrom = Constants.SDF_YMDHMS.format(findCostLineChartDto.getPaymentTimeFrom());
            formatPaymentTimeTo = Constants.SDF_YMDHMS.format(findCostLineChartDto.getPaymentTimeTo());
        } else if (DateScope.MONTH.getCode().equals(dateScope)) {
            formatPaymentTimeFrom = findCostLineChartDto.getYear() + "-" + StrUtil.fillBefore(String.valueOf(findCostLineChartDto.getMonth()), '0', 2);
        } else if (DateScope.YEAR.getCode().equals(dateScope)) {
            formatPaymentTimeFrom = String.valueOf(findCostLineChartDto.getYear());
        } else {
            throw new AccountException("dateScope.invalid", new Object[]{dateScope});
        }
        String cacheKey = "findCostLineChart:" + UString.join("@", String.valueOf(findCostLineChartDto.getUserId()), String.valueOf(findCostLineChartDto.getTenantId()), dateScope, formatPaymentTimeFrom, formatPaymentTimeTo, findCostLineChartDto.getCostType());
        EchartLineOptionEntity echartLineOptionCache = URedis.get(cacheKey);
        if (UEmpty.isNotNull(echartLineOptionCache)) {
            return echartLineOptionCache;
        }
        List<FindCostLineChartEntity> findCostLineChartEntityList;
        new EchartLineOptionEntity();
        EchartLineOptionEntity echartLineOptionEntity;
        if (DateScope.WEEK.getCode().equals(dateScope)) {
            findCostLineChartEntityList = accountCostMapper.findWeekCostLineChart(findCostLineChartDto);
            echartLineOptionEntity = buildEchartLineOptionEntity(findCostLineChartEntityList);
        } else if (DateScope.MONTH.getCode().equals(dateScope)) {
            findCostLineChartEntityList = accountCostMapper.findMonthCostLineChart(findCostLineChartDto);
            echartLineOptionEntity = buildEchartLineOptionEntity(findCostLineChartEntityList);
        } else {
            findCostLineChartEntityList = accountCostMapper.findYearCostLineChart(findCostLineChartDto);
            echartLineOptionEntity = buildEchartLineOptionEntity(findCostLineChartEntityList);
        }
        URedis.set(cacheKey, echartLineOptionEntity, Duration.ofSeconds(30));
        return echartLineOptionEntity;
    }

    @Override
    public EchartCalendarOptionEntity findCostSumCalendarNearYear(AccountCostDto accountCostDto) {
        String cacheKey = "findCostSumCalendarNearYear:" + accountCostDto.getUserId() + "@" + accountCostDto.getTenantId() + "@" + Constants.SDF_YMDHMS.format(accountCostDto.getPaymentTimeFrom()) + "@" + Constants.SDF_YMDHMS.format(accountCostDto.getPaymentTimeTo());
        EchartCalendarOptionEntity echartCalendarOptionEntityCache = URedis.get(cacheKey);
        if (UEmpty.isNotNull(echartCalendarOptionEntityCache)) {
            return echartCalendarOptionEntityCache;
        }
        List<FindCostSumCalendarNearYearEntity> findCostSumCalendarNearYearEntityList = accountCostMapper.findCostSumCalendarNearYear(accountCostDto);
        EchartCalendarOptionEntity echartCalendarOptionEntity = buildEchartCalendarOptionEntity(findCostSumCalendarNearYearEntityList, accountCostDto);
        if (UEmpty.isNotNull(echartCalendarOptionEntity)) {
            URedis.set(cacheKey, echartCalendarOptionEntity, Duration.ofHours(4));
        }
        return echartCalendarOptionEntity;
    }

    @Override
    public EchartStackedHorizontalBarOptionEntity findRankByCostType(FindRankByCostTypeDto findRankByCostTypeDto) {
        String cacheKey = "findRankByCostType:" + findRankByCostTypeDto.getUserId() + "@" + findRankByCostTypeDto.getTenantId() + "@" + findRankByCostTypeDto.getDateScope();
        EchartStackedHorizontalBarOptionEntity echartStackedHorizontalBarOptionEntity = URedis.get(cacheKey);
        if (UEmpty.isNotNull(echartStackedHorizontalBarOptionEntity)) {
            return echartStackedHorizontalBarOptionEntity;
        }
        String dateScope = findRankByCostTypeDto.getDateScope();
        List<FindRankByCostTypeEntity> findRankByCostTypeEntityList;
        EchartStackedHorizontalBarOptionEntity entity = null;
        if (DateScope.WEEK.getCode().equals(dateScope)) {
            findRankByCostTypeEntityList = accountCostMapper.findWeekRankByCostType(findRankByCostTypeDto);
            entity = buildWeekEchartStackedHorizontalBarOptionEntity(Optional.ofNullable(findRankByCostTypeEntityList).orElseGet(ArrayList::new));
        } else if (DateScope.MONTH.getCode().equals(dateScope)) {
            findRankByCostTypeEntityList = accountCostMapper.findMonthRankByCostType(findRankByCostTypeDto);
            entity = buildMonthEchartStackedHorizontalBarOptionEntity(Optional.ofNullable(findRankByCostTypeEntityList).orElseGet(ArrayList::new));
        }
        if (UEmpty.isNotNull(entity)) {
            URedis.set(cacheKey, entity, Duration.ofHours(4));
        }
        return entity;
    }

    @Override
    @Idempotent(interval = "PT10S")
    public void refreshCache() {
        Long userId = USecurity.getUserId();
        String findCostTypeRatePieCacheKey = "findCostTypeRatePie:" + userId + '*';
        String findCostLineChartCacheKey = "findCostLineChart:" + userId + '*';
        String findCostSumCalendarNearYearCacheKey = "findCostSumCalendarNearYear:" + userId + '*';
        String findRankByCostTypeCacheKey = "findRankByCostType:" + userId + '*';
        List<String> keyList = new ArrayList<>();
        keyList.addAll(URedis.scan(findCostTypeRatePieCacheKey));
        keyList.addAll(URedis.scan(findCostLineChartCacheKey));
        keyList.addAll(URedis.scan(findCostSumCalendarNearYearCacheKey));
        keyList.addAll(URedis.scan(findRankByCostTypeCacheKey));
        URedis.delete(keyList);
    }

    @Override
    public List<LaySelect> findSelectCostTypeList(AccountCostDto accountCostDto) {
        List<LaySelect> laySelectList = new ArrayList<>();
        List<AccountCostPo> accountCostPoList = accountCostMapper.findSelectCostTypeList(accountCostDto);
        if (UEmpty.isNotEmpty(accountCostPoList)) {
            for (AccountCostPo accountCostPo : accountCostPoList) {
                LaySelect laySelect = new LaySelect();
                laySelect.setLabel(accountCostPo.getCostType());
                laySelect.setValue(accountCostPo.getCostType());
                laySelectList.add(laySelect);
            }
        }
        return laySelectList;
    }

    @Override
    public List<FindCacheCostTypeEntity> findCacheCostType(AccountCostDto accountCostDto) {
        FindListSelectCostTypeDto dto = new FindListSelectCostTypeDto();
        dto.setUserId(accountCostDto.getUserId());
        dto.setValue(accountCostDto.getCostDesc());
        List<LaySelect> laySelectList = commonIconTemplateHeaderService.findCacheCostType(dto);
        List<FindCacheCostTypeEntity> findCacheCostTypeEntityList = UCollection.optimizeInitialCapacityArrayList(laySelectList.size());
        OssHandler ossHandler = OssFactory.getInstance();
        for (LaySelect laySelect : laySelectList) {
            FindCacheCostTypeEntity findCacheCostTypeEntity = new FindCacheCostTypeEntity();
            findCacheCostTypeEntity.setValue(laySelect.getLabel());
            findCacheCostTypeEntity.setIconUrl(ossHandler.convertEndpoint2Domain(laySelect.getValue()));
            findCacheCostTypeEntity.setDisabled(laySelect.getDisabled());
            findCacheCostTypeEntityList.add(findCacheCostTypeEntity);
        }
        return findCacheCostTypeEntityList;
    }

    @Override
    public List<LaySelect> findListSelectCostType(Long userId) {
        FindListSelectCostTypeDto dto = new FindListSelectCostTypeDto();
        dto.setUserId(userId);
        return commonIconTemplateHeaderService.findListSelectCostType(dto);
    }

    @Override
    public Date findMinPaymentTime(AccountCostDto accountCostDto) {
        return accountCostMapper.findMinPaymentTime(accountCostDto);
    }


    /**
     * 构建统计行
     *
     * @param accountCostExportEntityList 待导出的数据
     */
    private static void buildStatisticRow(List<AccountCostExportEntity> accountCostExportEntityList) {
        BigDecimal expenses = new BigDecimal(BigInteger.ZERO);
        BigDecimal income = new BigDecimal(BigInteger.ZERO);
        if (UEmpty.isNotEmpty(accountCostExportEntityList)) {
            for (AccountCostExportEntity accountCostExportEntity : accountCostExportEntityList) {
                BigDecimal outlay = accountCostExportEntity.getOutlay();
                if (outlay.compareTo(BigDecimal.ZERO) >= 0) {
                    String paymentSign = accountCostExportEntity.getPaymentSign();
                    if (CostType.EXPENSE.getCode().equals(paymentSign)) {
                        expenses = expenses.add(outlay);
                    } else if (CostType.INCOME.getCode().equals(paymentSign)) {
                        income = income.add(outlay);
                    }
                }
            }
            AccountCostExportEntity accountCostExportEntity = accountCostExportEntityList.get(accountCostExportEntityList.size() - 1);
            StringBuilder sb = new StringBuilder();
            income = income.setScale(2, RoundingMode.HALF_UP);
            expenses = expenses.setScale(2, RoundingMode.HALF_UP);
            sb.append("总计").append(income.subtract(expenses, MathContext.UNLIMITED)).append("元").append("，支出：").append(expenses).append("元").append("，收入：").append(income).append("元");
            accountCostExportEntity.setStatistic(sb.toString());
        }
    }

    /**
     * @param accountCostDto        入参
     * @param accountCostPo         待保存的实体
     * @param costId                收支记录主键
     * @param accountCostUserIdList 关联用户ID
     * @param userId                用户ID
     * @param sysUserDto            用户信息
     * @return 修改后的收支记录实体
     */
    private AccountCostPo handleUpdate(AccountCostDto accountCostDto, AccountCostPo accountCostPo, Long costId, List<Long> accountCostUserIdList, Long userId, SysUserDto sysUserDto) {
        // 独立事务更新
        AccountCostPo afterInsertAccountCostPo = accountCostRepository.save(accountCostPo);
        transactionTemplate.execute(status -> {
            if (UEmpty.isNotNull(costId)) {
                accountCostUserAllocService.deleteAccountCostUserAllocByCostId(Collections.singletonList(costId));
            }
            // 20251003-Bliss 添加费用分摊步骤
            List<AccountCostUserAllocDto> accountCostUserAllocDtoList = accountCostDto.getAccountCostUserAllocDtoList();
            if (UEmpty.isNotEmpty(accountCostUserAllocDtoList)) {
                BigDecimal outlay = accountCostDto.getOutlay();
                // 判断金额是否合法
                BigDecimal sumAmount = BigDecimal.ZERO;
                for (AccountCostUserAllocDto accountCostUserAllocDto : accountCostUserAllocDtoList) {
                    sumAmount = sumAmount.add(Convert.toBigDecimal(accountCostUserAllocDto.getAmount(), BigDecimal.ZERO));
                }
                List<AccountCostUserAllocDto> saveAccountCostUserAllocDtoList;
                if (UEmpty.isNotNull(afterInsertAccountCostPo) && UEmpty.isNotNull(outlay) && outlay.compareTo(BigDecimal.ZERO) > 0) {
                    saveAccountCostUserAllocDtoList = saveBatchAccountCostUserAllocDto(afterInsertAccountCostPo, accountCostUserAllocDtoList, outlay, sumAmount);
                } else {
                    throw new AccountException("account.cost.amount.invalid");
                }
                if (UEmpty.isNotEmpty(accountCostUserIdList)) {
                    if (UEmpty.isNotNull(afterInsertAccountCostPo)) {
                        List<Long> publishIdList = accountCostUserIdList.stream().filter(item -> !item.equals(userId)).collect(Collectors.toList());
                        AccountCostUserAllocDto accountCostUserAllocDto = saveAccountCostUserAllocDtoList.stream().filter(item -> Objects.equals(item.getUserId(), userId)).findFirst().orElseGet(AccountCostUserAllocDto::new);
                        if (UEmpty.isNotEmpty(saveAccountCostUserAllocDtoList) && UEmpty.isNotNull(accountCostUserAllocDto)) {
                            buildPublishMessage(publishIdList, "account.notice.modify", sysUserDto, afterInsertAccountCostPo, accountCostUserAllocDto);
                        } else {
                            buildPublishMessage(publishIdList, "account.notice.modify", sysUserDto, afterInsertAccountCostPo, null);
                        }
                    }
                }
            }
            return status;
        });
        return afterInsertAccountCostPo;
    }

    /**
     * 新增收支
     *
     * @param accountCostDto        入参
     * @param accountCostPo         待保存的实体
     * @param accountCostUserIdList 关联用户ID
     * @param userId                用户ID
     * @param sysUserDto            用户信息
     * @return 新增后的收支记录实体
     */
    private AccountCostPo handleInsert(AccountCostDto accountCostDto, AccountCostPo accountCostPo, List<Long> accountCostUserIdList, Long userId, SysUserDto sysUserDto) {
        AccountCostPo afterInsertAccountCostPo = accountCostRepository.save(accountCostPo);
        // 20251003-Bliss 添加费用分摊步骤
        List<AccountCostUserAllocDto> accountCostUserAllocDtoList = accountCostDto.getAccountCostUserAllocDtoList();
        if (UEmpty.isNotEmpty(accountCostUserAllocDtoList)) {
            BigDecimal outlay = accountCostDto.getOutlay();
            // 判断金额是否合法
            BigDecimal sumAmount = BigDecimal.ZERO;
            for (AccountCostUserAllocDto accountCostUserAllocDto : accountCostUserAllocDtoList) {
                sumAmount = sumAmount.add(Convert.toBigDecimal(accountCostUserAllocDto.getAmount(), BigDecimal.ZERO));
            }
            List<AccountCostUserAllocDto> saveAccountCostUserAllocDtoList;
            if (UEmpty.isNotNull(outlay) && outlay.compareTo(BigDecimal.ZERO) > 0) {
                saveAccountCostUserAllocDtoList = saveBatchAccountCostUserAllocDto(afterInsertAccountCostPo, accountCostUserAllocDtoList, outlay, sumAmount);
            } else {
                throw new AccountException("account.cost.amount.invalid");
            }
            if (UEmpty.isNotEmpty(accountCostUserIdList)) {
                List<Long> publishIdList = accountCostUserIdList.stream().filter(item -> !item.equals(userId)).collect(Collectors.toList());
                AccountCostUserAllocDto accountCostUserAllocDto = saveAccountCostUserAllocDtoList.stream().filter(item -> Objects.equals(item.getUserId(), userId)).findFirst().orElseGet(AccountCostUserAllocDto::new);
                if (UEmpty.isNotEmpty(saveAccountCostUserAllocDtoList) && UEmpty.isNotNull(accountCostUserAllocDto)) {
                    buildPublishMessage(publishIdList, "account.notice.add", sysUserDto, afterInsertAccountCostPo, accountCostUserAllocDto);
                } else {
                    buildPublishMessage(publishIdList, "account.notice.add", sysUserDto, afterInsertAccountCostPo, null);
                }
            }
        }
        return afterInsertAccountCostPo;
    }

    private List<AccountCostUserAllocDto> saveBatchAccountCostUserAllocDto(AccountCostPo afterInsertAccountCostPo, List<AccountCostUserAllocDto> accountCostUserAllocDtoList, BigDecimal outlay, BigDecimal sumAmount) {
        BigDecimal[] integerDivideResultArr = UCalculate.split(sumAmount, accountCostUserAllocDtoList.size());
        List<AccountCostUserAllocDto> saveAccountCostUserAllocDtoList;
        if (UCalculate.validateResult(outlay, integerDivideResultArr)) {
            // 如果总金额和分摊总金额匹配，则直接保存
            for (AccountCostUserAllocDto accountCostUserAllocDto : accountCostUserAllocDtoList) {
                accountCostUserAllocDto.setCostId(afterInsertAccountCostPo.getId());
                accountCostUserAllocDto.setUserId(accountCostUserAllocDto.getUserId());
                accountCostUserAllocDto.setTenantId(USecurity.getTenantId());
                accountCostUserAllocDto.setOperateTime(new Date());
            }
            saveAccountCostUserAllocDtoList = accountCostUserAllocDtoList;
            accountCostUserAllocService.saveUpdateBatch(saveAccountCostUserAllocDtoList);
        } else if (sumAmount.compareTo(BigDecimal.ZERO) == 0) {
            // 如果总金额不为0，但是各分摊为0，则默认平分
            integerDivideResultArr = UCalculate.split(outlay, accountCostUserAllocDtoList.size());
            int size = accountCostUserAllocDtoList.size();
            for (int i = 0; i < size; i++) {
                AccountCostUserAllocDto accountCostUserAllocDto = accountCostUserAllocDtoList.get(i);
                accountCostUserAllocDto.setCostId(afterInsertAccountCostPo.getId());
                accountCostUserAllocDto.setUserId(accountCostUserAllocDto.getUserId());
                accountCostUserAllocDto.setTenantId(USecurity.getTenantId());
                accountCostUserAllocDto.setAmount(integerDivideResultArr[i]);
                accountCostUserAllocDto.setOperateTime(new Date());
                accountCostUserAllocDto.setAllocFlag(true);
            }
            saveAccountCostUserAllocDtoList = accountCostUserAllocDtoList;
            accountCostUserAllocService.saveUpdateBatch(saveAccountCostUserAllocDtoList);
        } else {
            // 如果总金额和分摊总金额不匹配，则提示失败
            throw new AccountException("account.cost.amount.not.match");
        }
        return saveAccountCostUserAllocDtoList;
    }

    /**
     * 构建、发布信息
     *
     * @param publishIdList            消息接收用户ID
     * @param code                     消息i18n编码
     * @param sysUserDto               用户信息
     * @param afterInsertAccountCostPo 插入记账数据响应实体
     * @param accountCostUserAllocDto  费用分摊实体
     */
    private static void buildPublishMessage(List<Long> publishIdList, String code, SysUserDto sysUserDto, AccountCostPo afterInsertAccountCostPo, AccountCostUserAllocDto accountCostUserAllocDto) {
        // 构建消息
        SseMessageDto sseMessageDto = new SseMessageDto();
        sseMessageDto.setTopicList(Collections.singletonList(SseTopic.GLOBAL_SSE.getKey()));
        sseMessageDto.setUserIdList(publishIdList);
        BigDecimal outlay = afterInsertAccountCostPo.getOutlay();
        // 20251003-Bliss 消息接收人通知分摊金额
        if (UEmpty.isNotNull(accountCostUserAllocDto)) {
            outlay = Convert.toBigDecimal(accountCostUserAllocDto.getAmount(), BigDecimal.ZERO);
        }
        String message = UMessage.message(code, Objects.requireNonNull(sysUserDto).getNickName(), outlay, afterInsertAccountCostPo.getCostType(), Objects.requireNonNull(CostType.getInstanceByCode(afterInsertAccountCostPo.getPaymentSign())).getDesc());
        sseMessageDto.setContent(message);
        USse.publish(sseMessageDto);
        for (Long publicId : publishIdList) {
            UNotice.recordSysNotice(dto -> {
                dto.setTitle(UMessage.message("account.notice.cost.allocation"));
                dto.setType(SysNoticeType.NOTICE.getCode());
                dto.setContent(message);
                dto.setUserId(publicId);
                dto.setCategory(SysNoticeCategory.ACCOUNT.getCode());
                dto.setExcerpt(message);
                return dto;
            });
        }
    }

    private EchartStackedHorizontalBarOptionEntity buildWeekEchartStackedHorizontalBarOptionEntity(List<FindRankByCostTypeEntity> findRankByCostTypeEntityList) {
        EchartStackedHorizontalBarOptionEntity entity = new EchartStackedHorizontalBarOptionEntity();
        Function<FindRankByCostTypeEntity, String> groupingWeekStartEnd = item -> item.getWeekStart() + "\n" + item.getWeekEnd();
        Map<String, List<FindRankByCostTypeEntity>> groupingByDateSignMapList = findRankByCostTypeEntityList.stream().collect(Collectors.groupingBy(groupingWeekStartEnd));
        List<EchartStackedHorizontalBarOptionEntity.Series> sortedList = buildSortedSeries(groupingByDateSignMapList);
        List<String> dateSignList = findRankByCostTypeEntityList.stream().map(groupingWeekStartEnd).distinct().collect(Collectors.toList());
        entity.setYAxis(dateSignList);
        entity.setSeries(sortedList);
        return entity;
    }

    private EchartStackedHorizontalBarOptionEntity buildMonthEchartStackedHorizontalBarOptionEntity(List<FindRankByCostTypeEntity> findRankByCostTypeEntityList) {
        EchartStackedHorizontalBarOptionEntity entity = new EchartStackedHorizontalBarOptionEntity();
        Map<String, List<FindRankByCostTypeEntity>> groupingByDateSignMapList = findRankByCostTypeEntityList.stream().collect(Collectors.groupingBy(FindRankByCostTypeEntity::getDateSign));
        List<EchartStackedHorizontalBarOptionEntity.Series> sortedList = buildSortedSeries(groupingByDateSignMapList);
        List<String> dateSignList = findRankByCostTypeEntityList.stream().map(FindRankByCostTypeEntity::getDateSign).distinct().collect(Collectors.toList());
        entity.setYAxis(dateSignList);
        entity.setSeries(sortedList);
        return entity;
    }

    private List<EchartStackedHorizontalBarOptionEntity.Series> buildSortedSeries(Map<String, List<FindRankByCostTypeEntity>> groupingByDateSignMapList) {
        Set<Map.Entry<String, List<FindRankByCostTypeEntity>>> entrySet = groupingByDateSignMapList.entrySet();
        Map<String, List<BigDecimal>> resultMap = new HashMap<>(16);
        for (Map.Entry<String, List<FindRankByCostTypeEntity>> entry : entrySet) {
            List<FindRankByCostTypeEntity> groupingDateSignList = entry.getValue();
            int size = entrySet.size();
            for (FindRankByCostTypeEntity item : groupingDateSignList) {
                resultMap.computeIfAbsent(item.getCostType(), e -> {
                    List<BigDecimal> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        list.add(i, null);
                    }
                    return list;
                }).set(item.getRk() - 1, item.getOutlay().setScale(2, RoundingMode.FLOOR));
            }
        }
        List<EchartStackedHorizontalBarOptionEntity.Series> seriesList = new ArrayList<>();
        resultMap.forEach((k, v) -> {
            EchartStackedHorizontalBarOptionEntity.Series series = new EchartStackedHorizontalBarOptionEntity.Series(k, v);
            seriesList.add(series);
        });
        return seriesList.stream().sorted(Comparator.comparing(item -> item.getValue().stream().filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add))).collect(Collectors.toList());
    }

    private EchartCalendarOptionEntity buildEchartCalendarOptionEntity(List<FindCostSumCalendarNearYearEntity> findCostSumCalendarNearYearEntityList, AccountCostDto accountCostDto) {
        EchartCalendarOptionEntity echartCalendarOptionEntity = new EchartCalendarOptionEntity();
        if (UEmpty.isNotEmpty(findCostSumCalendarNearYearEntityList)) {
            List<List<String>> series = buildSeries(findCostSumCalendarNearYearEntityList);
            BigDecimal maxValue = findCostSumCalendarNearYearEntityList.stream().map(FindCostSumCalendarNearYearEntity::getOutlay).filter(Objects::nonNull).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            echartCalendarOptionEntity.setMaxValue(maxValue);
            String paymentTimeFrom = Constants.SDF_YMD.format(accountCostDto.getPaymentTimeFrom());
            String paymentTimeTo = Constants.SDF_YMD.format(accountCostDto.getPaymentTimeTo());
            echartCalendarOptionEntity.setRange(new String[]{paymentTimeFrom, paymentTimeTo});
            echartCalendarOptionEntity.setSeries(series);
        }
        return echartCalendarOptionEntity;
    }

    private static List<List<String>> buildSeries(List<FindCostSumCalendarNearYearEntity> findCostSumCalendarNearYearEntityList) {
        List<List<String>> series = new ArrayList<>();
        for (FindCostSumCalendarNearYearEntity findCostSumCalendarNearYearEntity : findCostSumCalendarNearYearEntityList) {
            BigDecimal outlay = findCostSumCalendarNearYearEntity.getOutlay();
            if (UEmpty.isNotNull(outlay)) {
                outlay = outlay.setScale(2, RoundingMode.HALF_UP);
                String paymentTime = findCostSumCalendarNearYearEntity.getPaymentTime();
                List<String> seriesList = Arrays.asList(paymentTime, outlay.toString());
                series.add(seriesList);
            }
        }
        return series;
    }

    private EchartLineOptionEntity buildEchartLineOptionEntity(List<FindCostLineChartEntity> findCostLineChartEntityList) {
        EchartLineOptionEntity echartLineOptionEntity = new EchartLineOptionEntity();
        if (UEmpty.isNotEmpty(findCostLineChartEntityList)) {
            List<EchartLineOptionEntity.Series> series = new ArrayList<>();
            EchartLineOptionEntity.Series tmp = new EchartLineOptionEntity.Series();
            BigDecimal[] outlays = findCostLineChartEntityList.stream().map(FindCostLineChartEntity::getOutlay).toArray(BigDecimal[]::new);
            List<String> xAxis = findCostLineChartEntityList.stream().map(FindCostLineChartEntity::getXAxis).collect(Collectors.toList());
            tmp.setData(outlays);
            series.add(tmp);
            echartLineOptionEntity.setSeries(series);
            echartLineOptionEntity.setXAxis(xAxis);
        }
        return echartLineOptionEntity;
    }
}
