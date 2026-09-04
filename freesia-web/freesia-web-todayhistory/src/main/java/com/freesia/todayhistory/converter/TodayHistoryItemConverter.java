package com.freesia.todayhistory.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.todayhistory.dto.TodayHistoryItemDto;
import com.freesia.todayhistory.po.TodayHistoryItemPo;
import com.freesia.todayhistory.vo.TodayHistoryItemVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 历史上的今天-条目表 MapStruct转换器
 * @date 2026-09-04
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TodayHistoryItemConverter extends MapStructConverter<TodayHistoryItemVo, TodayHistoryItemDto, TodayHistoryItemPo> {
}
