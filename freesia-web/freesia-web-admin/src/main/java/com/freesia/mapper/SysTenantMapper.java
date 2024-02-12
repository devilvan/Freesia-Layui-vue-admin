package com.freesia.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.po.SysTenantPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 租户信息表 持久层
 * @date 2024-01-31
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenantPo> {
    /**
     * 查询租户编码是否存在
     *
     * @param code 租户编码
     * @return 租户编码是否存在
     */
    Integer findExistCode(@Param("code") String code);

    /**
     * 根据用户ID查询对应的租户
     *
     * @param id 用户ID
     * @return 租户ID
     */
    List<Long> findSysTenantUser(@Param("userId") Long id);

    /**
     * 根据租户ID查询
     *
     * @param tenantIdList 租户ID
     * @return 租户信息
     */
    List<SysTenantPo> findListSysTenantById(@Param("tenantIdList") List<Long> tenantIdList);
}
