package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.UrlConfigDto;
import com.freesia.po.UrlConfigPo;
import com.freesia.vo.UrlConfigVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description URL配置 转换器
 * @date 2026-01-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UrlConfigConverter extends MapStructConverter<UrlConfigVo, UrlConfigDto, UrlConfigPo> {
}
