package com.freesia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.po.SysColumnDetailPo;
import com.freesia.dto.SysColumnDetailDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 持久层
 * @date 2026-03-27
 */
@Mapper
public interface SysColumnDetailMapper extends BaseMapper<SysColumnDetailPo> {
    /**
     * 分页查询系统列明细表信息
     *
     * @param sysColumnDetailDto 查询条件
     * @param page               分页条件
     * @return 分页信息
     */
    Page<SysColumnDetailPo> findPage(@Param(value = "dto") SysColumnDetailDto sysColumnDetailDto, @Param("page") Page<SysColumnDetailPo> page);

    /**
     * 查询系统列明细表信息
     *
     * @param sysColumnDetailDto 查询条件
     * @return 分页信息
     */
    List<SysColumnDetailPo> findList(@Param(value = "dto") SysColumnDetailDto sysColumnDetailDto);

    /**
     * 查询系统列明细表信息
     *
     * @param sysColumnDetailDto 查询条件
     * @return 分页信息
     */
    SysColumnDetailPo findOne(@Param(value = "dto") SysColumnDetailDto sysColumnDetailDto);

    /**
     * 批量新增
     *
     * @param list 待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<SysColumnDetailPo> list);

    /**
     * 根据中间表过滤后的明细
     *
     * @param sysColumnDetailDto 查询条件
     * @return 结果集
     */
    List<SysColumnDetailDto> findMiddleList(SysColumnDetailDto sysColumnDetailDto);
}
