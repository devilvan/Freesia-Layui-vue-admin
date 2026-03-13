package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysClientDto;
import com.freesia.po.SysClientPo;
import com.freesia.vo.SysClientVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统用户授权表 MapStruct转换器
 * @date 2026-03-13
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysClientConverter extends MapStructConverter<SysClientVo, SysClientDto, SysClientPo> {
}
