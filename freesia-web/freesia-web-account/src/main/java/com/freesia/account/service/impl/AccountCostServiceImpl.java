package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.account.constant.CostType;
import com.freesia.account.constant.DateScope;
import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.entity.*;
import com.freesia.account.mapper.AccountCostMapper;
import com.freesia.account.po.AccountCostPo;
import com.freesia.account.po.AccountCostUserPk;
import com.freesia.account.po.AccountCostUserPo;
import com.freesia.account.repository.AccountCostRepository;
import com.freesia.account.repository.AccountCostUserRepository;
import com.freesia.account.service.AccountCostService;
import com.freesia.constant.Constants;
import com.freesia.entity.EchartCalendarOptionEntity;
import com.freesia.entity.EchartLineOptionEntity;
import com.freesia.entity.EchartPieOptionEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.util.UStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 开销表 业务逻辑类
 * @date 2024-12-14
 */
@Service
@RequiredArgsConstructor
public class AccountCostServiceImpl extends ServiceImpl<AccountCostMapper, AccountCostPo> implements AccountCostService {
    private final AccountCostRepository accountCostRepository;
    private final AccountCostMapper accountCostMapper;
    private final AccountCostUserRepository accountCostUserRepository;
    private final TransactionTemplate transactionTemplate;

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
            sb.append("总计").append(income.subtract(expenses, MathContext.UNLIMITED)).append("元")
                    .append("，支出：").append(expenses).append("元")
                    .append("，收入：").append(income).append("元");
            accountCostExportEntity.setStatistic(sb.toString());
        }
    }

    @Override
    public AccountCostDto saveUpdate(AccountCostDto accountCostDto) {
        Long costId = accountCostDto.getId();
        AccountCostPo accountCostPo = UCopy.copyDto2Po(accountCostDto, AccountCostPo.class);
        UCopy.halfCopy(accountCostDto, accountCostPo);
        Set<AccountCostUserPo> accountCostUserPoSet = new HashSet<>();
        List<Long> accountCostUserIdList = accountCostDto.getAccountCostUserIdList();
        // 新增
        if (UEmpty.isNull(costId)) {
            AccountCostPo afterInsertAccountCostPo = accountCostRepository.save(accountCostPo);
            if (UEmpty.isNotEmpty(accountCostUserIdList)) {
                for (Long accountCostUserId : accountCostUserIdList) {
                    AccountCostUserPo accountCostUserPo = new AccountCostUserPo(new AccountCostUserPk(afterInsertAccountCostPo.getId(), accountCostUserId));
                    accountCostUserPoSet.add(accountCostUserPo);
                }
            }
            accountCostUserRepository.saveAll(accountCostUserPoSet);
            return UCopy.copyPo2Dto(afterInsertAccountCostPo, AccountCostDto.class);
        } else {
            // 独立事务更新
            AccountCostPo afterTransactionSaveAccountCostPo = transactionTemplate.execute(status -> {
                // 修改
                if (UEmpty.isNotNull(costId)) {
                    accountCostUserRepository.deleteByCostId(costId);
                }
                if (UEmpty.isNotEmpty(accountCostUserIdList)) {
                    for (Long accountCostUserId : accountCostUserIdList) {
                        AccountCostUserPo accountCostUserPo = new AccountCostUserPo(new AccountCostUserPk(accountCostPo.getId(), accountCostUserId));
                        accountCostUserPoSet.add(accountCostUserPo);
                    }
                }
                accountCostUserRepository.saveAll(accountCostUserPoSet);
                return accountCostRepository.save(accountCostPo);
            });
            return UCopy.copyPo2Dto(afterTransactionSaveAccountCostPo, AccountCostDto.class);
        }
    }

    @Override
    public List<AccountCostDto> saveUpdateBatch(List<AccountCostDto> list) {
        List<AccountCostPo> accountCostPoList = UCopy.fullCopyList(list, AccountCostPo.class);
        return UCopy.fullCopyList(accountCostRepository.saveAllAndFlush(accountCostPoList), AccountCostDto.class);
    }

    @Override
    public TableResult<FindPageAccountCostEntity> findPageAccountCost(AccountCostDto accountCost, PageQuery pageQuery) {
        Page<FindPageAccountCostEntity> pagePo = accountCostMapper.findPageAccountCost(accountCost, pageQuery.build());
        return TableResult.build(pagePo);
    }

    @Override
    public FindAccountCostEntity findAccountCost(AccountCostDto accountCost) {
        return accountCostMapper.findAccountCost(accountCost);
    }

    @Override
    @Transactional
    public void deleteAccountCost(List<Long> idList) {
        List<AccountCostPo> accountCostPoList = accountCostRepository.findAllById(idList);
        Set<AccountCostUserPo> accountCostUserPoSet = new HashSet<>();
        for (AccountCostPo accountCostPo : accountCostPoList) {
            accountCostUserPoSet.addAll(accountCostPo.getAccountCostUserPoSet());
        }
        accountCostUserRepository.deleteAllInBatch(accountCostUserPoSet);
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
        return UCopy.fullCopyList(toExportList, AccountCostExportEntity.class);
    }

    @Override
    public EchartPieOptionEntity findCostTypeRatePie(AccountCostDto accountCostDto) {
        List<FindCostTypeRatePieEntity> accountCostPoList = accountCostMapper.findCostTypeRatePie(accountCostDto);
        EchartPieOptionEntity echartPieOptionEntity = new EchartPieOptionEntity();
        if (UEmpty.isNotEmpty(accountCostPoList)) {
            Set<String> legendSet = accountCostPoList.stream().map(FindCostTypeRatePieEntity::getCostType).collect(Collectors.toSet());
            echartPieOptionEntity.setLegends(legendSet);
            List<EchartPieOptionEntity.Series> series = new ArrayList<>();
            for (FindCostTypeRatePieEntity findCostTypeRatePieEntity : accountCostPoList) {
                EchartPieOptionEntity.Series tmp = new EchartPieOptionEntity.Series();
                tmp.setName(findCostTypeRatePieEntity.getCostType());
                tmp.setValue(findCostTypeRatePieEntity.getOutlay().setScale(2, RoundingMode.HALF_UP).toString());
                series.add(tmp);
            }
            echartPieOptionEntity.setSeries(series);
            return echartPieOptionEntity;
        }
        return null;
    }

    @Override
    public EchartLineOptionEntity findCostLineChart(AccountCostDto accountCostDto) {
        String dateScope = accountCostDto.getDateScope();
        List<FindCostLineChartEntity> findCostLineChartEntityList;
        EchartLineOptionEntity echartLineOptionEntity = new EchartLineOptionEntity();
        if (DateScope.WEEK.getCode().equals(dateScope)) {
            findCostLineChartEntityList = accountCostMapper.findWeekCostLineChart(accountCostDto);
            echartLineOptionEntity = buildEchartLineOptionEntity(findCostLineChartEntityList);
        } else if (DateScope.MONTH.getCode().equals(dateScope)) {
            findCostLineChartEntityList = accountCostMapper.findMonthCostLineChart(accountCostDto);
            echartLineOptionEntity = buildEchartLineOptionEntity(findCostLineChartEntityList);
        } else if (DateScope.YEAR.getCode().equals(dateScope)) {
            findCostLineChartEntityList = accountCostMapper.findYearCostLineChart(accountCostDto);
            echartLineOptionEntity = buildEchartLineOptionEntity(findCostLineChartEntityList);
        }
        return echartLineOptionEntity;
    }

    @Override
    public EchartCalendarOptionEntity findCostSumCalendarNearYear(AccountCostDto accountCostDto) {
        List<FindCostSumCalendarNearYearEntity> findCostSumCalendarNearYearEntityList = accountCostMapper.findCostSumCalendarNearYear(accountCostDto);
        return buildEchartCalendarOptionEntity(findCostSumCalendarNearYearEntityList, accountCostDto);
    }

    private EchartCalendarOptionEntity buildEchartCalendarOptionEntity(List<FindCostSumCalendarNearYearEntity> findCostSumCalendarNearYearEntityList, AccountCostDto accountCostDto) {
        EchartCalendarOptionEntity echartCalendarOptionEntity = new EchartCalendarOptionEntity();
        if (UEmpty.isNotEmpty(findCostSumCalendarNearYearEntityList)) {
            List<List<String>> series = new ArrayList<>();
            for (FindCostSumCalendarNearYearEntity findCostSumCalendarNearYearEntity : findCostSumCalendarNearYearEntityList) {
                BigDecimal outlay = findCostSumCalendarNearYearEntity.getOutlay().setScale(2, RoundingMode.HALF_UP);
                String paymentTime = findCostSumCalendarNearYearEntity.getPaymentTime();
                List<String> seriesList = Arrays.asList(paymentTime, outlay.toString());
                series.add(seriesList);
            }
            BigDecimal maxValue = findCostSumCalendarNearYearEntityList.stream()
                    .map(FindCostSumCalendarNearYearEntity::getOutlay)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            echartCalendarOptionEntity.setMaxValue(maxValue);
            String paymentTimeFrom = Constants.SDF_YMD.format(accountCostDto.getPaymentTimeFrom());
            String paymentTimeTo = Constants.SDF_YMD.format(accountCostDto.getPaymentTimeTo());
            echartCalendarOptionEntity.setRange(new String[]{paymentTimeFrom, paymentTimeTo});
            echartCalendarOptionEntity.setSeries(series);
        }
        return echartCalendarOptionEntity;
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
