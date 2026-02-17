package com.freesia.account.repository;


import com.freesia.account.po.AccountBillingPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 记账账单表 持久层
 * @date 2026-02-17
 */
@Repository
public interface AccountBillingRepository extends JpaRepository<AccountBillingPo, Long> {
}
