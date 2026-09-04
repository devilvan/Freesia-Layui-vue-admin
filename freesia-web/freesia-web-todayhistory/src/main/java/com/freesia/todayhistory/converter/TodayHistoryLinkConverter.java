package com.freesia.todayhistory.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.todayhistory.dto.TodayHistoryLinkDto;
import com.freesia.todayhistory.po.TodayHistoryLinkPo;
import com.freesia.todayhistory.vo.TodayHistoryLinkVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 历史上的今天-链接表 MapStruct转换器
 * @date 2026-09-04
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TodayHistoryLinkConverter extends MapStructConverter<TodayHistoryLinkVo, TodayHistoryLinkDto, TodayHistoryLinkPo> {
}
