package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindCurrentUserProfileEntity;
import com.freesia.entity.FindPageSysUserListEntity;
import com.freesia.po.SysUserPo;
import com.freesia.vo.SysUserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统用户 转换器
 * @date 2026-01-18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SysUserConverter extends MapStructConverter<SysUserVo, SysUserDto, SysUserPo> {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "idList", ignore = true)
    SysUserDto convertVo2Dto(SysUserVo source);

    @Mapping(target = "sysUserRolePoSet", ignore = true)
    @Mapping(target = "sysTenantUserPoSet", ignore = true)
    @Mapping(target = "sysRolePoSet", ignore = true)
    @Mapping(target = "sysDeptPo", ignore = true)
    SysUserPo convertDto2Po(SysUserDto source);

    @Mapping(target = "idList", ignore = true)
    @Mapping(target = "createTimeTo", ignore = true)
    @Mapping(target = "createTimeFrom", ignore = true)
    SysUserDto convertPo2Dto(SysUserPo source);

    List<FindPageSysUserListEntity> convertPo2FindPageSysUserListEntity(List<SysUserPo> sourceList);

    FindCurrentUserProfileEntity convertDto2FindCurrentUserProfileEntity(SysUserDto sysUserDto);

    @Mapping(target = "sysUserRolePoSet", ignore = true)
    @Mapping(target = "sysTenantUserPoSet", ignore = true)
    @Mapping(target = "sysRolePoSet", ignore = true)
    @Mapping(target = "sysDeptPo", ignore = true)
    void updateSysUserDto2Po(SysUserDto sysUserDto, @MappingTarget SysUserPo sysUserPo);
}
