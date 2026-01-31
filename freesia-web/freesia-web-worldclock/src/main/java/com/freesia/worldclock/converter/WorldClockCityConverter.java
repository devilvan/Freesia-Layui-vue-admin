package com.freesia.worldclock.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.worldclock.dto.WorldClockCityDto;
import com.freesia.worldclock.po.WorldClockCityPo;
import com.freesia.worldclock.vo.WorldClockCityVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 世界时钟城市 转换器
 * @date 2026-01-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WorldClockCityConverter extends MapStructConverter<WorldClockCityVo, WorldClockCityDto, WorldClockCityPo> {
    @Mapping(target = "cityNameList", ignore = true)
    @Override
    WorldClockCityDto convertPo2Dto(WorldClockCityPo source);

    WorldClockCityDto convertWorldClockCityVo2Dto(WorldClockCityVo worldClockCityVo);

}
