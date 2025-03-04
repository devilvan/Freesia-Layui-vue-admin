package com.freesia.account.repository;


import com.freesia.account.po.AccountBudgetPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 开销-预算表 持久层
 * @date 2025-03-04
 */
@Repository
public interface AccountBudgetRepository extends JpaRepository<AccountBudgetPo, Long> {
}
