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
     * 根据角色ID删除 部门-角色信息表中的数据
     *
     * @param roleId 用户ID
     */
    @Modifying
    @Query(value = """
                DELETE FROM SysRoleDeptPo WHERE sysRoleDeptPk.roleId = :roleId
            """)
    void removeDeptRelationByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询默认角色
     *
     * @return 默认角色信息
     */
    @Query(value = """
                SELECT sysRolePo FROM SysRolePo sysRolePo WHERE sysRolePo.roleKey = :roleKey
            """)
    SysRolePo findCommonRole(@Param("roleKey") String roleKey);

}
