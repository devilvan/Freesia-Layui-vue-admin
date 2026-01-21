package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysDictKeyDto;
import com.freesia.po.SysDictKeyPo;
import com.freesia.vo.SysDictKeyVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 系统字典键 转换器
 * @date 2026-01-18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysDictKeyConverter extends MapStructConverter<SysDictKeyVo, SysDictKeyDto, SysDictKeyPo> {
}
