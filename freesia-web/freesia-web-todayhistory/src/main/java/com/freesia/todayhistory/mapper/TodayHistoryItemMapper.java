package com.freesia.todayhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.todayhistory.po.TodayHistoryItemPo;
import com.freesia.todayhistory.dto.TodayHistoryItemDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 历史上的今天-条目表 持久层
 * @date 2026-09-04
 */
@Mapper
public interface TodayHistoryItemMapper extends BaseMapper<TodayHistoryItemPo> {
    /**
     * 分页查询历史上的今天-条目表信息
     *
     * @param todayHistoryItemDto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<TodayHistoryItemPo> findPage(@Param(value = "dto") TodayHistoryItemDto todayHistoryItemDto, @Param("page") Page<TodayHistoryItemPo> page);

    /**
     * 查询历史上的今天-条目表信息
     *
     * @param todayHistoryItemDto 查询条件
     * @return 分页信息
     */
    List<TodayHistoryItemPo> findList(@Param(value = "dto") TodayHistoryItemDto todayHistoryItemDto);

    /**
     * 查询历史上的今天-条目表信息
     *
     * @param todayHistoryItemDto 查询条件
     * @return 分页信息
     */
    TodayHistoryItemPo findOne(@Param(value = "dto") TodayHistoryItemDto todayHistoryItemDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<TodayHistoryItemPo> list);
}
