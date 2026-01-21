package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysRoleDto;
import com.freesia.entity.FindAllRolesEntity;
import com.freesia.po.SysRolePo;
import com.freesia.vo.SaveRoleVo;
import com.freesia.vo.SysRoleVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 角色 转换器
 * @date 2026-01-18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysRoleConverter extends MapStructConverter<SysRoleVo, SysRoleDto, SysRolePo> {
    @Mapping(target = "menuCheckStrictly", ignore = true)
    @Mapping(target = "deptCheckStrictly", ignore = true)
    SysRoleDto convertSaveVo2Dto(SaveRoleVo saveRoleVo);

    List<FindAllRolesEntity> convertBatchPo2FindAllRolesEntity(List<SysRolePo> sysRolePoList);

    @Mapping(target = "sysUserRolePoSet", ignore = true)
    @Mapping(target = "sysUserPoSet", ignore = true)
    @Mapping(target = "sysRoleMenuPoSet", ignore = true)
    @Mapping(target = "sysRoleDeptPoSet", ignore = true)
    @Mapping(target = "sysMenuPoSet", ignore = true)
    @Mapping(target = "sysDeptPoSet", ignore = true)
    void updateSysRoleDto2Po(SysRoleDto sysRoleDto, @MappingTarget SysRolePo sysRolePo);
}
