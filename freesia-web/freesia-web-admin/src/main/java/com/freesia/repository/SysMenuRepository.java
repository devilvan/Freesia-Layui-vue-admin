package com.freesia.repository;


import com.freesia.po.SysMenuPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 目录/菜单/按钮信息表 持久层
 * @date 2023-08-12
 */
@Repository
public interface SysMenuRepository extends JpaRepository<SysMenuPo, Long> {
    /**
     * 根据菜单ID删除 角色-菜单中间表
     *
     * @param idList 菜单ID
     */
    @Modifying
    @Query(value = """
                DELETE FROM SysRoleMenuPo WHERE sysRoleMenuPk.menuId in (:idList)
            """)
    @Transactional(rollbackFor = Exception.class)
    void deleteRoleMenu(@Param("idList") List<Long> idList);

    /**
     * 根据路径查询菜单
     *
     * @param path 路径列表
     * @return 菜单列表
     */
    @Query(value = """
                SELECT sysMenuPo FROM SysMenuPo sysMenuPo WHERE sysMenuPo.logicDel = false and  sysMenuPo.path IN (:path)
            """)
    List<SysMenuPo> findByPathIn(@Param("path") List<String> path);

    /**
     * 根据权限查询菜单
     *
     * @param permission 权限列表
     * @return 菜单列表
     */
    @Query(value = """
                SELECT sysMenuPo FROM SysMenuPo sysMenuPo WHERE sysMenuPo.logicDel = false and  sysMenuPo.perms IN (:permission)
            """)
    List<SysMenuPo> findByPermsIn(@Param("permission") List<String> permission);
}
