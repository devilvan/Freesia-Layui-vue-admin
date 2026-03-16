package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysColumnDetailDto;
import com.freesia.po.SysColumnDetailPo;
import com.freesia.vo.SysColumnDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 MapStruct转换器
 * @date 2026-03-16
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysColumnDetailConverter extends MapStructConverter<SysColumnDetailVo, SysColumnDetailDto, SysColumnDetailPo> {
}
