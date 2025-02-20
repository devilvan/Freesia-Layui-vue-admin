package com.freesia.repository;


import com.freesia.po.SysRoleMenuPk;
import com.freesia.po.SysRoleMenuPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 角色-菜单关联 持久层
 * @date 2024-07-26
 */
@Repository
public interface SysRoleMenuRepository extends JpaRepository<SysRoleMenuPo, SysRoleMenuPk> {
}
