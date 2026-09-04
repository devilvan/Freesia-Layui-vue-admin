package com.freesia.todayhistory.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.todayhistory.dto.TodayHistoryPageDto;
import com.freesia.todayhistory.po.TodayHistoryPagePo;
import com.freesia.todayhistory.vo.TodayHistoryPageVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 历史上的今天-页面表 MapStruct转换器
 * @date 2026-09-04
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TodayHistoryPageConverter extends MapStructConverter<TodayHistoryPageVo, TodayHistoryPageDto, TodayHistoryPagePo> {
}
