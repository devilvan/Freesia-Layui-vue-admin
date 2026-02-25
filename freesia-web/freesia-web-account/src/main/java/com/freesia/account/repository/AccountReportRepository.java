package com.freesia.account.repository;


import com.freesia.account.po.AccountReportPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账报表表 持久层
 * @date 2026-02-25
 */
@Repository
public interface AccountReportRepository extends JpaRepository<AccountReportPo, Long> {
}
