package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysColumnMiddleDto;
import com.freesia.po.SysColumnMiddlePo;
import com.freesia.vo.SysColumnMiddleVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列中间表 MapStruct转换器
 * @date 2026-03-20
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysColumnMiddleConverter extends MapStructConverter<SysColumnMiddleVo, SysColumnMiddleDto, SysColumnMiddlePo> {
}
