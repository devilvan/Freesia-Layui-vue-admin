package com.freesia.repository;


import com.freesia.po.SysDeptPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 部门信息表 持久层
 * @date 2023-08-12
 */
@Repository
public interface SysDeptRepository extends JpaRepository<SysDeptPo, Long> {
    /**
     * 根据部门ID删除 部门-角色信息表中的数据
     *
     * @param deptId 用户ID
     */
    @Modifying
    @Query(value = """
                DELETE FROM SysRoleDeptPo WHERE sysRoleDeptPk.deptId = :deptId
            """)
    void removeRelationByDeptId(@Param("deptId") Long deptId);
}
