package com.freesia.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.account.constant.CostType;
import com.freesia.account.constant.DateScope;
import com.freesia.account.dto.AccountCostDto;
import com.freesia.account.entity.AccountCostExportEntity;
import com.freesia.account.entity.FindCostLineChartEntity;
import com.freesia.account.entity.FindCostTypeRatePieEntity;
import com.freesia.account.mapper.AccountCostMapper;
import com.freesia.account.po.AccountCostPo;
import com.freesia.account.repository.AccountCostRepository;
import com.freesia.account.service.AccountCostService;
import com.freesia.constant.FlagConstant;
import com.freesia.entity.EchartLineOptionEntity;
import com.freesia.entity.EchartPieOptionEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.util.UStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private static LambdaQueryWrapper<AccountCostPo> buildAccountCostPoLambdaQueryWrapper(AccountCostDto accountCost) {
        return new LambdaQueryWrapper<AccountCostPo>()
                .eq(AccountCostPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountCost.getId()), AccountCostPo::getId, accountCost.getId())
                .like(UEmpty.isNotEmpty(accountCost.getCostDesc()), AccountCostPo::getCostDesc, accountCost.getCostDesc())
                .like(UEmpty.isNotEmpty(accountCost.getRemark()), AccountCostPo::getRemark, accountCost.getRemark())
                .eq(UEmpty.isNotEmpty(accountCost.getPaymentSign()), AccountCostPo::getPaymentSign, accountCost.getPaymentSign())
                .between(UEmpty.isNotEmpty(accountCost.getPaymentTimeFrom()) && UEmpty.isNotEmpty(accountCost.getPaymentTimeTo()),
                        AccountCostPo::getPaymentTime,
                        accountCost.getPaymentTimeFrom(),
                        accountCost.getPaymentTimeTo())
                .orderByDesc(AccountCostPo::getPaymentTime);
    }

    @Override
    public AccountCostDto saveUpdate(AccountCostDto accountCostDto) {
        AccountCostPo accountCostPo = new AccountCostPo();
        UCopy.fullCopy(accountCostDto, accountCostPo);
        return UCopy.copyPo2Dto(accountCostRepository.saveAndFlush(accountCostPo), AccountCostDto.class);
    }

    @Override
    public List<AccountCostDto> saveUpdateBatch(List<AccountCostDto> list) {
        List<AccountCostPo> accountCostPoList = UCopy.fullCopyList(list, AccountCostPo.class);
        return UCopy.fullCopyList(accountCostRepository.saveAllAndFlush(accountCostPoList), AccountCostDto.class);
    }

    @Override
    public TableResult<AccountCostDto> findPageAccountCost(AccountCostDto accountCost, PageQuery pageQuery) {
        LambdaQueryWrapper<AccountCostPo> wrapper = buildAccountCostPoLambdaQueryWrapper(accountCost);
        Page<AccountCostPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, AccountCostDto.class));
    }

    @Override
    public AccountCostDto findAccountCost(AccountCostDto accountCost) {
        Long tenantId = USecurity.getTenantId();
        LambdaQueryWrapper<AccountCostPo> wrapper = new LambdaQueryWrapper<AccountCostPo>()
                .eq(AccountCostPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(accountCost.getId()), AccountCostPo::getId, accountCost.getId())
                .eq(UEmpty.isNotEmpty(tenantId), AccountCostPo::getTenantId, tenantId);
        return UCopy.copyPo2Dto(getOne(wrapper), AccountCostDto.class);
    }

    @Override
    @Transactional
    public void deleteAccountCost(List<Long> idList) {
        removeBatchByIds(idList);
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
