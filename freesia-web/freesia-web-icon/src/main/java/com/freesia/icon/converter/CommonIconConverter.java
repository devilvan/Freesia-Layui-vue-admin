package com.freesia.icon.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.icon.dto.CommonIconDto;
import com.freesia.icon.po.CommonIconPo;
import com.freesia.icon.vo.CommonIconVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 图标 转换器
 * @date 2026-01-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommonIconConverter extends MapStructConverter<CommonIconVo, CommonIconDto, CommonIconPo> {
}
