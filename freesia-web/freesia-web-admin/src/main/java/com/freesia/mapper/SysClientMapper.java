package com.freesia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.po.SysClientPo;
import com.freesia.dto.SysClientDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统用户授权表 持久层
 * @date 2026-03-13
 */
@Mapper
public interface SysClientMapper extends BaseMapper<SysClientPo> {
    /**
     * 分页查询系统用户授权表信息
     *
     * @param sysClientDto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<SysClientPo> findPage(@Param(value = "dto") SysClientDto sysClientDto, @Param("page") Page<SysClientPo> page);

    /**
     * 查询系统用户授权表信息
     *
     * @param sysClientDto 查询条件
     * @return 分页信息
     */
    List<SysClientDto> findList(@Param(value = "dto") SysClientDto sysClientDto);

    /**
     * 查询系统用户授权表信息
     *
     * @param sysClientDto 查询条件
     * @return 分页信息
     */
    SysClientPo findOne(@Param(value = "dto") SysClientDto sysClientDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<SysClientPo> list);
}
