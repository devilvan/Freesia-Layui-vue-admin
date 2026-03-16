package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysColumnHeaderDto;
import com.freesia.po.SysColumnHeaderPo;
import com.freesia.vo.SysColumnHeaderVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列头表 MapStruct转换器
 * @date 2026-03-16
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysColumnHeaderConverter extends MapStructConverter<SysColumnHeaderVo, SysColumnHeaderDto, SysColumnHeaderPo> {
}
