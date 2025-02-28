package com.freesia.account.repository;


import com.freesia.account.po.AccountCostUserPk;
import com.freesia.account.po.AccountCostUserPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 开销-用户关联表 持久层
 * @date 2025-02-28
 */
@Repository
public interface AccountCostUserRepository extends JpaRepository<AccountCostUserPo, AccountCostUserPk> {
    /**
     * 根据开支ID 删除开销-用户关联表
     *
     * @param costId 开支ID
     */
    @Modifying
    @Query(value = """
                DELETE FROM AccountCostUserPo WHERE accountCostUserPk.costId = :costId
            """)
    void deleteByCostId(@Param("costId") Long costId);
}
