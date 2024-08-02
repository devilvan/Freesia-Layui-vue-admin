package com.freesia.repository;


import com.freesia.po.SysUserRolePk;
import com.freesia.po.SysUserRolePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 用户-角色关联 持久层
 * @date 2024-07-26
 */
@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRolePo, SysUserRolePk> {
}
