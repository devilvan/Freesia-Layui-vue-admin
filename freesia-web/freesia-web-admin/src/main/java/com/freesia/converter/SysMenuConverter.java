package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysMenuDto;
import com.freesia.entity.FindAllMenuTreeEntity;
import com.freesia.po.SysMenuPo;
import com.freesia.vo.SysMenuVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 菜单 转换器
 * @date 2026-01-18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysMenuConverter extends MapStructConverter<SysMenuVo, SysMenuDto, SysMenuPo> {
    List<FindAllMenuTreeEntity> convertBatchPo2FindAllMenuTreeEntity(List<SysMenuPo> sysMenuPoList);
}
