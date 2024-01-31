package com.freesia.repository;


import com.freesia.po.SysTenantPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 租户信息表 持久层
 * @date 2024-01-31
 */
@Repository
public interface SysTenantRepository extends JpaRepository<SysTenantPo, Long> {
}
