package com.freesia.worldclock.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.worldclock.dto.WorldClockSunriseSunsetDto;
import com.freesia.worldclock.po.WorldClockSunriseSunsetPo;
import com.freesia.worldclock.vo.WorldClockSunriseSunsetVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 世界时钟日出日落 转换器
 * @date 2026-01-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WorldClockSunriseSunsetConverter extends MapStructConverter<WorldClockSunriseSunsetVo, WorldClockSunriseSunsetDto, WorldClockSunriseSunsetPo> {
}
