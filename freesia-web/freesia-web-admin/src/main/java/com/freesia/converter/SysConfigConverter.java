package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysConfigDto;
import com.freesia.po.SysConfigPo;
import com.freesia.vo.SysConfigVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 系统配置 转换器
 * @date 2026-01-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysConfigConverter extends MapStructConverter<SysConfigVo, SysConfigDto, SysConfigPo> {
}
