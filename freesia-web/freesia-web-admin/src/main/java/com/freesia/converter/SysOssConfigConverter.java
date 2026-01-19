package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysOssConfigDto;
import com.freesia.po.SysOssConfigPo;
import com.freesia.vo.SysOssConfigVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description oss 配置信息表 转换器
 * @date 2026-01-18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysOssConfigConverter extends MapStructConverter<SysOssConfigVo, SysOssConfigDto, SysOssConfigPo> {
}
