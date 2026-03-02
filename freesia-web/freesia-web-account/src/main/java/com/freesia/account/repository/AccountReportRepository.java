package com.freesia.account.repository;


import com.freesia.account.po.AccountReportPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 记账报表表 持久层
 * @date 2026-02-25
 */
@Repository
public interface AccountReportRepository extends JpaRepository<AccountReportPo, Long> {
    @Modifying
    @Query(value = """
                UPDATE AccountReportPo SET recalculateFlag = false WHERE id in (:idSet)
            """)
    @Transactional(rollbackFor = Exception.class)
    void changeRecalculateFlag(@Param("idSet") Set<Long> idSet);
}
