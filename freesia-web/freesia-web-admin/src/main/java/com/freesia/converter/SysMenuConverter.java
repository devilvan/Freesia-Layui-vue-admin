package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysMenuDto;
import com.freesia.po.SysMenuPo;
import com.freesia.vo.SysMenuVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * @author Evad.Wu
 * @Description 菜单 转换器
 * @date 2026-01-18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysMenuConverter extends MapStructConverter<SysMenuVo, SysMenuDto, SysMenuPo> {
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "parentName", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Override
    SysMenuDto convertVo2Dto(SysMenuVo source);

    @Mapping(target = "sysRolePoSet", ignore = true)
    @Mapping(target = "sysRoleMenuPoSet", ignore = true)
    @Override
    SysMenuPo convertDto2Po(SysMenuDto source);

    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "parentName", ignore = true)
    @Mapping(target = "componentType", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Override
    SysMenuDto convertPo2Dto(SysMenuPo source);
}
