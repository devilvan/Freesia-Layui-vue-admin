package com.freesia.repository;


import com.freesia.po.SysUserRolePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 用户-角色关联信息表 持久层
 * @date 2023-08-12
 */
@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRolePo, Long> {
    /**
     * 根据用户ID删除 用户-角色信息表中的数据
     *
     * @param userId 用户ID
     */
    @Modifying
    @Query(value = """
                DELETE FROM SysUserRolePo WHERE sysRoleMenuPk.userId = :userId
            """)
    void removeRelationByUserId(@Param("userId") Long userId);
}
