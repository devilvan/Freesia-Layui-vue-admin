package com.freesia.repository;


import com.freesia.po.SysTenantUserPk;
import com.freesia.po.SysTenantUserPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 租户-用户关联 持久层
 * @date 2026-06-03
 */
@Repository
public interface SysTenantUserRepository extends JpaRepository<SysTenantUserPo, SysTenantUserPk> {
}
