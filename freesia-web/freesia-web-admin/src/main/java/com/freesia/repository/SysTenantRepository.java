package com.freesia.repository;


import com.freesia.po.SysTenantPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 租户信息表 持久层
 * @date 2024-01-31
 */
@Repository
public interface SysTenantRepository extends JpaRepository<SysTenantPo, Long> {
    /**
     * 为用户分配租户
     *
     * @param tenantId   租户ID
     * @param userIdList 用户ID
     */
    @Modifying
    @Query(value = """
            UPDATE SysUserPo
                SET tenantId = :tenantId
            WHERE 1=1
                AND id IN (:userIdList)
            """)
    @Transactional(rollbackFor = Exception.class)
    void assignTenant2User(@Param("tenantId") Long tenantId, @Param("userIdList") List<Long> userIdList);

    /**
     * 取消将租户分配给用户
     *
     * @param tenantId   租户ID
     * @param userIdList 待取消分配的用户ID
     */
    @Modifying
    @Query(value = """
            DELETE FROM SysTenantUserPo TU
            WHERE 1=1
                AND TU.sysTenantUserPk.tenantId = :tenantId
                AND TU.sysTenantUserPk.userId IN (:userIdList)
            """)
    @Transactional(rollbackFor = Exception.class)
    void cancelAssignUser(@Param("tenantId") Long tenantId, @Param("userIdList") List<Long> userIdList);

    /**
     * 取消将租户分配给用户
     *
     * @param idList 租户ID
     */
    @Modifying
    @Query(value = """
            UPDATE SysTenantPo
                SET logicDel = 1
            WHERE 1=1
                AND id IN (:idList)
            """)
    @Transactional(rollbackFor = Exception.class)
    void updateLogicDel(@Param("idList") List<Long> idList);

    /**
     * 查询租户编码是否存在
     *
     * @param code 租户编码
     * @return 租户编码是否存在
     */
    @Query(value = """
            SELECT 1
            FROM SysTenantPo
            WHERE 1=1
                AND code = :code
            """)
    Integer findExistCode(@Param("code") String code);
}
