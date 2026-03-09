package com.freesia.account.scheduler;

import com.freesia.account.AccountReportSchedulerHelper;
import com.freesia.account.dto.AccountReportDto;
import com.freesia.account.entity.FindPageAccountCostEntity;
import com.freesia.account.service.AccountCostService;
import com.freesia.account.service.AccountReportService;
import com.freesia.constant.BudgetType;
import com.freesia.dto.BaseDto;
import com.freesia.util.UEmpty;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bliss.Wu
 * @Description 重算报表 定时器
 * @date 2026-03-02
 */
@Component
@RequiredArgsConstructor
public class RecalculateReportScheduler {
    private final AccountReportService accountReportService;
    private final AccountCostService accountCostService;

    /**
     * 重算日报表
     */
    @XxlJob("recalculateDayReport")
    public ReturnT<String> recalculateDayReport() {
        List<Long> idList = findRecalculateIdList(BudgetType.DAY);
        if (UEmpty.isEmpty(idList)) {
            return ReturnT.FAIL;
        }
        handleRecalculateReport(idList);
        return ReturnT.SUCCESS;
    }

    /**
     * 重算周报表
     */
    @XxlJob("recalculateWeekReport")
    public ReturnT<String> recalculateWeekReport() {
        List<Long> idList = findRecalculateIdList(BudgetType.WEEK);
        if (UEmpty.isEmpty(idList)) {
            return ReturnT.FAIL;
        }
        handleRecalculateReport(idList);
        return ReturnT.SUCCESS;
    }

    /**
     * 重算月报表
     */
    @XxlJob("recalculateMonthReport")
    public ReturnT<String> recalculateMonthReport() {
        List<Long> idList = findRecalculateIdList(BudgetType.MONTH);
        if (UEmpty.isEmpty(idList)) {
            return ReturnT.FAIL;
        }
        handleRecalculateReport(idList);
        return ReturnT.SUCCESS;
    }

    /**
     * 重算年报表
     */
    @XxlJob("recalculateYearReport")
    public ReturnT<String> recalculateYearReport() {
        List<Long> idList = findRecalculateIdList(BudgetType.YEAR);
        if (UEmpty.isEmpty(idList)) {
            return ReturnT.FAIL;
        }
        handleRecalculateReport(idList);
        return ReturnT.SUCCESS;
    }


    /**
     * 查询重算标识为false的报表数据
     *
     * @return 报表ID
     */
    private List<Long> findRecalculateIdList(BudgetType budgetType) {
        AccountReportDto accountReportDto = new AccountReportDto();
        accountReportDto.setRecalculateFlag(false);
        accountReportDto.setBudgetType(budgetType.getCode());
        List<AccountReportDto> accountReportDtoList = accountReportService.findList(accountReportDto);
        return accountReportDtoList.stream().map(BaseDto::getId).collect(Collectors.toList());
    }

    private void handleRecalculateReport(List<Long> idList) {
        AccountReportDto queryParam = new AccountReportDto();
        queryParam.setIdList(idList);
        List<AccountReportDto> accountReportDtoList = accountReportService.findList(queryParam);
        if (UEmpty.isEmpty(accountReportDtoList)) {
            return;
        }
        for (AccountReportDto accountReportDto : accountReportDtoList) {
            Long userId = accountReportDto.getUserId();
            Date billingTimeFrom = accountReportDto.getBillingTimeFrom();
            Date billingTimeTo = accountReportDto.getBillingTimeTo();
            // 查询期间收支记录
            List<FindPageAccountCostEntity> findAccountCostEntityList = AccountReportSchedulerHelper.findListAccountCost(accountCostService, userId, billingTimeFrom, billingTimeTo);
            // 构建收支金额
            AccountReportSchedulerHelper.buildReportOutlayIncome(userId, findAccountCostEntityList, accountReportDto);
            accountReportDto.setRecalculateFlag(true);
        }
        accountReportService.saveUpdateBatch(accountReportDtoList);
    }
}
