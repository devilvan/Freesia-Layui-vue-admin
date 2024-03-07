package com.freesia.service;

import com.freesia.dto.SysTenantDto;
import com.freesia.entity.FindSysTenantEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 租户信息表 业务逻辑接口
 * @date 2024-01-31
 */
public interface SysTenantService {
    /**
     * 保存租户信息表信息
     *
     * @param sysTenantDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysTenantDto saveUpdate(SysTenantDto sysTenantDto);

    /**
     * 批量保存租户信息表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysTenantDto> saveUpdateBatch(List<SysTenantDto> list);

    /**
     * 查询租户信息表信息
     *
     * @param sysTenantDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<SysTenantDto> findPageSysTenant(SysTenantDto sysTenantDto, PageQuery pageQuery);

    /**
     * 条件查询租户信息表信息
     *
     * @param sysTenantDto 查询条件
     * @return 租户信息表信息
     */
    FindSysTenantEntity findSysTenant(SysTenantDto sysTenantDto);

    /**
     * 删除租户信息表信息
     *
     * @param idList 主键
     */
    void deleteSysTenant(List<Long> idList);

    /**
     * 为用户分配租户
     *
     * @param tenantId   租户ID
     * @param userIdList 用户ID
     */
    void assignTenant2User(Long tenantId, List<Long> userIdList);

    /**
     * 取消将租户分配给用户
     *
     * @param tenantId   租户ID
     * @param userIdList 待取消分配的用户ID
     */
    void cancelAssignUser(Long tenantId, List<Long> userIdList);

    /**
     * 根据用户ID查询对应租户
     *
     * @param userId 用户ID
     * @return 租户信息
     */
    List<SysTenantDto> findListSysTenantByUserId(Long userId);
}
