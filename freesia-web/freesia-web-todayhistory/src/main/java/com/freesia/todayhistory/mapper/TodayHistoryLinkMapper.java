package com.freesia.todayhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.todayhistory.dto.TodayHistoryLinkDto;
import com.freesia.todayhistory.po.TodayHistoryLinkPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 历史上的今天-链接持久层.
 */
@Mapper
public interface TodayHistoryLinkMapper extends BaseMapper<TodayHistoryLinkPo> {
    Page<TodayHistoryLinkPo> findPage(@Param("dto") TodayHistoryLinkDto todayHistoryLinkDto, @Param("page") Page<TodayHistoryLinkPo> page);

    List<TodayHistoryLinkPo> findList(@Param("dto") TodayHistoryLinkDto todayHistoryLinkDto);

    TodayHistoryLinkPo findOne(@Param("dto") TodayHistoryLinkDto todayHistoryLinkDto);

    List<TodayHistoryLinkPo> findByPageIdOrderBySortNoAsc(@Param("pageId") Long pageId);

    List<TodayHistoryLinkPo> findByItemIdInOrderByItemIdAscSortNoAsc(@Param("itemIds") Collection<Long> itemIds);

    int deleteByPageId(@Param("pageId") Long pageId);

    int insertBatch(@Param("list") List<TodayHistoryLinkPo> list);
}
