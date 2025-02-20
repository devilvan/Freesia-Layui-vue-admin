package com.freesia.account.repository;


import com.freesia.account.po.AccountCostPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 开销表 持久层
 * @date 2024-12-14
 */
@Repository
public interface AccountCostRepository extends JpaRepository<AccountCostPo, Long> {
}
