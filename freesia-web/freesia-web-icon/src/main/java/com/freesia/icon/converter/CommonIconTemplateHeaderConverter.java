package com.freesia.icon.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.icon.dto.CommonIconTemplateHeaderDto;
import com.freesia.icon.po.CommonIconTemplateHeaderPo;
import com.freesia.icon.vo.CommonIconTemplateHeaderVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 图标模板头 转换器
 * @date 2026-01-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommonIconTemplateHeaderConverter extends MapStructConverter<CommonIconTemplateHeaderVo, CommonIconTemplateHeaderDto, CommonIconTemplateHeaderPo> {
    @Mapping(target = "commonIconTemplateDetailPoSet", ignore = true)
    @Override
    CommonIconTemplateHeaderPo convertDto2Po(CommonIconTemplateHeaderDto source);
}
