package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysSensitiveLogDto;
import com.freesia.po.SysSensitiveLogPo;
import com.freesia.vo.SysSensitiveLogVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 敏感日志 转换器
 * @date 2026-01-18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysSensitiveLogConverter extends MapStructConverter<SysSensitiveLogVo, SysSensitiveLogDto, SysSensitiveLogPo> {
}
