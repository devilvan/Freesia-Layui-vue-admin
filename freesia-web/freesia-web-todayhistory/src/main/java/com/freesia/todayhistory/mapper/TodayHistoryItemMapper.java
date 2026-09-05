package com.freesia.todayhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.todayhistory.dto.TodayHistoryItemDto;
import com.freesia.todayhistory.dto.TodayHistorySearchResultDto;
import com.freesia.todayhistory.po.TodayHistoryItemPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 历史上的今天-条目持久层.
 */
@Mapper
public interface TodayHistoryItemMapper extends BaseMapper<TodayHistoryItemPo> {
    Page<TodayHistoryItemPo> findPage(@Param("dto") TodayHistoryItemDto todayHistoryItemDto, @Param("page") Page<TodayHistoryItemPo> page);

    List<TodayHistoryItemPo> findList(@Param("dto") TodayHistoryItemDto todayHistoryItemDto);

    TodayHistoryItemPo findOne(@Param("dto") TodayHistoryItemDto todayHistoryItemDto);

    List<TodayHistoryItemPo> findByPageIdOrderBySortNoAsc(@Param("pageId") Long pageId);

    int deleteByPageId(@Param("pageId") Long pageId);

    List<TodayHistorySearchResultDto> searchGlobal(@Param("keyword") String keyword);

    int insertBatch(@Param("list") List<TodayHistoryItemPo> list);
}
