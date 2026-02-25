package com.freesia.account.repository;


import com.freesia.account.po.AccountReportStrategyPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账报表策略表 持久层
 * @date 2026-02-25
 */
@Repository
public interface AccountReportStrategyRepository extends JpaRepository<AccountReportStrategyPo, Long> {
}
