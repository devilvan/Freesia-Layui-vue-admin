package com.freesia.todayhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.todayhistory.po.TodayHistoryPagePo;
import com.freesia.todayhistory.dto.TodayHistoryPageDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 历史上的今天-页面表 持久层
 * @date 2026-09-04
 */
@Mapper
public interface TodayHistoryPageMapper extends BaseMapper<TodayHistoryPagePo> {
    /**
     * 分页查询历史上的今天-页面表信息
     *
     * @param todayHistoryPageDto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<TodayHistoryPagePo> findPage(@Param(value = "dto") TodayHistoryPageDto todayHistoryPageDto, @Param("page") Page<TodayHistoryPagePo> page);

    /**
     * 查询历史上的今天-页面表信息
     *
     * @param todayHistoryPageDto 查询条件
     * @return 分页信息
     */
    List<TodayHistoryPagePo> findList(@Param(value = "dto") TodayHistoryPageDto todayHistoryPageDto);

    /**
     * 查询历史上的今天-页面表信息
     *
     * @param todayHistoryPageDto 查询条件
     * @return 分页信息
     */
    TodayHistoryPagePo findOne(@Param(value = "dto") TodayHistoryPageDto todayHistoryPageDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<TodayHistoryPagePo> list);
}
