package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysThirdpartyAuthDto;
import com.freesia.po.SysThirdpartyAuthPo;
import com.freesia.vo.SysThirdpartyAuthVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 第三方平台授权表 MapStruct转换器
 * @date 2026-03-13
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysThirdpartyAuthConverter extends MapStructConverter<SysThirdpartyAuthVo, SysThirdpartyAuthDto, SysThirdpartyAuthPo> {
}
