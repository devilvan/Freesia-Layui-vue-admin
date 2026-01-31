package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysSensitiveLogDto;
import com.freesia.po.SysSensitiveLogPo;
import com.freesia.vo.SysSensitiveLogVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 敏感日志 转换器
 * @date 2026-01-18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysSensitiveLogConverter extends MapStructConverter<SysSensitiveLogVo, SysSensitiveLogDto, SysSensitiveLogPo> {
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "os", ignore = true)
    @Mapping(target = "operatorId", ignore = true)
    @Mapping(target = "operateTime", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    @Mapping(target = "deptId", ignore = true)
    @Mapping(target = "contextOld", ignore = true)
    @Mapping(target = "context", ignore = true)
    @Mapping(target = "browser", ignore = true)
    @Mapping(target = "beOperatedName", ignore = true)
    @Mapping(target = "beOperatedId", ignore = true)
    @Override
    SysSensitiveLogDto convertVo2Dto(SysSensitiveLogVo source);

    @Override
    SysSensitiveLogPo convertDto2Po(SysSensitiveLogDto source);

    @Mapping(target = "operateTimeTo", ignore = true)
    @Mapping(target = "operateTimeFrom", ignore = true)
    @Override
    SysSensitiveLogDto convertPo2Dto(SysSensitiveLogPo source);
}
