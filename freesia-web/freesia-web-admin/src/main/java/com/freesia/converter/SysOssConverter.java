package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysOssDto;
import com.freesia.po.SysOssPo;
import com.freesia.vo.SysOssVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description oss 转换器
 * @date 2026-01-18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysOssConverter extends MapStructConverter<SysOssVo, SysOssDto, SysOssPo> {
}
