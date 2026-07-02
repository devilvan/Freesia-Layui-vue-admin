package com.freesia.account.repository;


import com.freesia.account.po.AccountCostUserAllocPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 费用分摊表 持久层
 * @date 2025-10-03
 */
@Repository
public interface AccountCostUserAllocRepository extends JpaRepository<AccountCostUserAllocPo, Long> {
    /**
     * 根据记账ID删除
     *
     * @param costIdList 记账ID
     */
    @Modifying
    @Query(value = """
                DELETE FROM AccountCostUserAllocPo WHERE costId IN (:costIdList)
            """)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    void deleteAccountCostUserAllocByCostId(@Param(value = "costIdList") List<Long> costIdList);

    /**
     * 根据记账ID集合查询费用分摊记录
     *
     * @param costIdList 记账ID集合
     * @return 费用分摊记录
     */
    List<AccountCostUserAllocPo> findByCostIdIn(@Param(value = "costIdList") List<Long> costIdList);
}
