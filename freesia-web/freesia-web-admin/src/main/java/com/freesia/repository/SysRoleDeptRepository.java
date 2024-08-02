package com.freesia.repository;


import com.freesia.po.SysRoleDeptPk;
import com.freesia.po.SysRoleDeptPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 角色-部门关联 持久层
 * @date 2024-07-26
 */
@Repository
public interface SysRoleDeptRepository extends JpaRepository<SysRoleDeptPo, SysRoleDeptPk> {
}
