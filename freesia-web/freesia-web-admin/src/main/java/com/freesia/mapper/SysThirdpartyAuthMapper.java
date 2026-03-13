package com.freesia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.po.SysThirdpartyAuthPo;
import com.freesia.dto.SysThirdpartyAuthDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 第三方平台授权表 持久层
 * @date 2026-03-13
 */
@Mapper
public interface SysThirdpartyAuthMapper extends BaseMapper<SysThirdpartyAuthPo> {
    /**
     * 分页查询第三方平台授权表信息
     *
     * @param sysThirdpartyAuthDto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<SysThirdpartyAuthPo> findPage(@Param(value = "dto") SysThirdpartyAuthDto sysThirdpartyAuthDto, @Param("page") Page<SysThirdpartyAuthPo> page);

    /**
     * 查询第三方平台授权表信息
     *
     * @param sysThirdpartyAuthDto 查询条件
     * @return 分页信息
     */
    List<SysThirdpartyAuthDto> findList(@Param(value = "dto") SysThirdpartyAuthDto sysThirdpartyAuthDto);

    /**
     * 查询第三方平台授权表信息
     *
     * @param sysThirdpartyAuthDto 查询条件
     * @return 分页信息
     */
    SysThirdpartyAuthPo findOne(@Param(value = "dto") SysThirdpartyAuthDto sysThirdpartyAuthDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<SysThirdpartyAuthPo> list);
}
