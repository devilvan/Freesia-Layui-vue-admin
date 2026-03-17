package com.freesia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.po.SysColumnHeaderPo;
import com.freesia.dto.SysColumnHeaderDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列头表 持久层
 * @date 2026-03-17
 */
@Mapper
public interface SysColumnHeaderMapper extends BaseMapper<SysColumnHeaderPo> {
    /**
     * 分页查询系统列头表信息
     *
     * @param sysColumnHeaderDto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<SysColumnHeaderPo> findPage(@Param(value = "dto") SysColumnHeaderDto sysColumnHeaderDto, @Param("page") Page<SysColumnHeaderPo> page);

    /**
     * 查询系统列头表信息
     *
     * @param sysColumnHeaderDto 查询条件
     * @return 分页信息
     */
    List<SysColumnHeaderDto> findList(@Param(value = "dto") SysColumnHeaderDto sysColumnHeaderDto);

    /**
     * 查询系统列头表信息
     *
     * @param sysColumnHeaderDto 查询条件
     * @return 分页信息
     */
    SysColumnHeaderPo findOne(@Param(value = "dto") SysColumnHeaderDto sysColumnHeaderDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<SysColumnHeaderPo> list);
}
