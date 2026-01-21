package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysDeptDto;
import com.freesia.po.SysDeptPo;
import com.freesia.vo.SaveDeptVo;
import com.freesia.vo.SysDeptVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

/**
 * @author Evad.Wu
 * @Description 系统部门 转换器
 * @date 2026-01-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysDeptConverter extends MapStructConverter<SysDeptVo, SysDeptDto, SysDeptPo> {
    @Mapping(target = "sysUserPoSet", ignore = true)
    @Mapping(target = "sysRolePoSet", ignore = true)
    @Mapping(target = "sysRoleDeptPoSet", ignore = true)
    void updateSysDeptDto2Po(SysDeptDto sysDeptDto, @MappingTarget SysDeptPo sysDeptPo);

    SysDeptDto convertSaveDeptVo2Dto(SaveDeptVo saveDeptVo);
}
