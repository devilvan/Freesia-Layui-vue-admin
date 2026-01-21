package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysDictDto;
import com.freesia.dto.SysDictValueDto;
import com.freesia.po.SysDictValuePo;
import com.freesia.vo.SysDictValueVo;
import com.freesia.vo.SysDictVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

/**
 * @author Evad.Wu
 * @Description 系统字典值 转换器
 * @date 2026-01-18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysDictValueConverter extends MapStructConverter<SysDictValueVo, SysDictValueDto, SysDictValuePo> {
    @Mapping(target = "sysDictKeyPo", ignore = true)
    void updateSysDictValueDto2Po(SysDictValueDto sysDictValueDto, @MappingTarget SysDictValuePo sysDictValuePo);

    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "orderNum", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    @Mapping(target = "i18n", ignore = true)
    @Mapping(target = "cssStyle", ignore = true)
    SysDictValueDto convertSysDictVo2SysDictValueDto(SysDictVo sysDictValueVo);

    SysDictDto convertSysDictVo2Dto(SysDictVo sysDictVo);
}
