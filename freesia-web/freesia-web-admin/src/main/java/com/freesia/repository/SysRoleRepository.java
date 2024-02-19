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
    /**
     * 取消给用户分配角色
     *
     * @param roleId     角色ID
     * @param userIdList 取消分配的用户ID
     */
    @Modifying
    @Query(value = """
                DELETE FROM SysUserRolePo WHERE sysRoleMenuPk.roleId = :roleId and sysRoleMenuPk.userId in (:userIdList)
            """)
    @Transactional(rollbackFor = Exception.class)
    void cancelAssignUser(@Param("roleId") Long roleId, @Param("userIdList") List<Long> userIdList);

    /**
     * 根据角色ID，删除角色-菜单关联表中已经分配的按钮
     *
     * @param roleId             角色ID
     * @param removeButtonIdList 待删除的已分配的按钮ID
     */
    @Modifying
    @Query(value = """
            DELETE FROM SysRoleMenuPo SRM
            WHERE 1=1
            AND SRM.sysRoleMenuPk.roleId = :roleId
            AND SRM.sysRoleMenuPk.menuId IN (:removeButtonIdList)
            """)
    void removeRelationByRoleId(@Param("roleId") Long roleId, @Param("removeButtonIdList") List<Long> removeButtonIdList);

}
