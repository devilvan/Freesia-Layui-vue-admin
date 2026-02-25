package com.freesia.account.repository;


import com.freesia.account.po.AccountReportPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账报表表 持久层
 * @date 2026-02-25
 */
@Repository
public interface AccountReportRepository extends JpaRepository<AccountReportPo, Long> {
    /**
     * 根据预算ID、预算类型和账单开始时间查询账单是否存在
     *
     * @param budgetId        预算ID
     * @param budgetType      预算类型
     * @param billingTimeFrom 账单开始时间
     * @return 账单列表
     */
    List<AccountReportPo> findByBudgetIdAndBudgetTypeAndBillingTimeFrom(Long budgetId, String budgetType, Date billingTimeFrom);
}
