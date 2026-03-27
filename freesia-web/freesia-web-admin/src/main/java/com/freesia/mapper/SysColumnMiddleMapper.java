package com.freesia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.po.SysColumnMiddlePo;
import com.freesia.dto.SysColumnMiddleDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列中间表 持久层
 * @date 2026-03-27
 */
@Mapper
public interface SysColumnMiddleMapper extends BaseMapper<SysColumnMiddlePo> {
    /**
     * 分页查询系统列中间表信息
     *
     * @param sysColumnMiddleDto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<SysColumnMiddlePo> findPage(@Param(value = "dto") SysColumnMiddleDto sysColumnMiddleDto, @Param("page") Page<SysColumnMiddlePo> page);

    /**
     * 查询系统列中间表信息
     *
     * @param sysColumnMiddleDto 查询条件
     * @return 分页信息
     */
    List<SysColumnMiddleDto> findList(@Param(value = "dto") SysColumnMiddleDto sysColumnMiddleDto);

    /**
     * 查询系统列中间表信息
     *
     * @param sysColumnMiddleDto 查询条件
     * @return 分页信息
     */
    SysColumnMiddlePo findOne(@Param(value = "dto") SysColumnMiddleDto sysColumnMiddleDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<SysColumnMiddlePo> list);
}
