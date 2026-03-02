package com.freesia.account.scheduler;

import com.freesia.account.AccountReportSchedulerHelper;
import com.freesia.account.dto.AccountReportDto;
import com.freesia.account.entity.FindPageAccountCostEntity;
import com.freesia.account.service.AccountCostService;
import com.freesia.account.service.AccountReportService;
import com.freesia.dto.BaseDto;
import com.freesia.util.UEmpty;
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
    public void recalculateDayReport() {
        List<Long> idList = findRecalculateIdList();
        if (UEmpty.isEmpty(idList)) {
            return;
        }
        handleRecalculateReport(idList);
    }

    /**
     * 重算周报表
     */
    @XxlJob("recalculateWeekReport")
    public void recalculateWeekReport() {
        List<Long> idList = findRecalculateIdList();
        if (UEmpty.isEmpty(idList)) {
            return;
        }
        handleRecalculateReport(idList);
    }

    /**
     * 重算月报表
     */
    @XxlJob("recalculateMonthReport")
    public void recalculateMonthReport() {
        List<Long> idList = findRecalculateIdList();
        if (UEmpty.isEmpty(idList)) {
            return;
        }
        handleRecalculateReport(idList);
    }

    /**
     * 重算年报表
     */
    @XxlJob("recalculateYearReport")
    public void recalculateYearReport() {
        List<Long> idList = findRecalculateIdList();
        if (UEmpty.isEmpty(idList)) {
            return;
        }
        handleRecalculateReport(idList);
    }


    /**
     * 查询重算标识为false的报表数据
     *
     * @return 报表ID
     */
    private List<Long> findRecalculateIdList() {
        AccountReportDto accountReportDto = new AccountReportDto();
        accountReportDto.setRecalculateFlag(false);
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
            Long tenantId = accountReportDto.getTenantId();
            Date billingTimeFrom = accountReportDto.getBillingTimeFrom();
            Date billingTimeTo = accountReportDto.getBillingTimeTo();
            // 查询期间收支记录
            List<FindPageAccountCostEntity> findAccountCostEntityList = AccountReportSchedulerHelper.findListAccountCost(accountCostService, userId, tenantId, billingTimeFrom, billingTimeTo);
            // 构建收支金额
            AccountReportSchedulerHelper.buildReportOutlayIncome(userId, findAccountCostEntityList, accountReportDto);
        }
        accountReportService.saveUpdateBatch(accountReportDtoList);
    }
}
