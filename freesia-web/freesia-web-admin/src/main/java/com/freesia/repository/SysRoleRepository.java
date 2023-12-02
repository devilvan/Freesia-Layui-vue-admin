package com.freesia.repository;


import com.freesia.po.SysRolePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 角色信息表 持久层
 * @date 2023-08-12
 */
@Repository
public interface SysRoleRepository extends JpaRepository<SysRolePo, Long> {
}
