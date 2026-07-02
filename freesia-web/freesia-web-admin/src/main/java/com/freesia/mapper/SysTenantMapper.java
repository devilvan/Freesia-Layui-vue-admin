package com.freesia.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.annotation.DataColumn;
import com.freesia.annotation.DataPermission;
import com.freesia.dto.SysTenantDto;
import com.freesia.po.SysTenantPo;
import com.freesia.pojo.TableResult;
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
     * 根据用户ID查询对应租户
     *
     * @param userId 用户ID
     * @return 租户信息
     */
    List<SysTenantPo> findListSysTenantByUserId(@Param("userId") Long userId);

    /**
     * 查询租户信息表信息
     *
     * @param page    信息
     * @param wrapper 查询条件
     * @return 分页信息
     */
    @DataPermission({
            @DataColumn(key = "userName", value = "STU.USER_ID"),
    })
    Page<SysTenantPo> findPageSysTenant(@Param("page") Page<SysTenantPo> page, @Param(Constants.WRAPPER) Wrapper<SysTenantPo> wrapper);

    /**
     * 分页查询租户信息表信息
     *
     * @param page 分页参数
     * @param dto  查询参数
     * @return 结果集
     */
    Page<SysTenantDto> findPage(@Param("page") Page<SysTenantPo> page, @Param("dto") SysTenantDto dto);
}
