package com.freesia.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindCurrentUserProfileEntity;
import com.freesia.po.SysUserPo;
import com.freesia.vo.SysUserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

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

    FindCurrentUserProfileEntity convertDto2FindCurrentUserProfileEntity(SysUserDto sysUserDto);

    @Mapping(target = "sysUserRolePoSet", ignore = true)
    @Mapping(target = "sysTenantUserPoSet", ignore = true)
    @Mapping(target = "sysRolePoSet", ignore = true)
    @Mapping(target = "sysDeptPo", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "modifier", ignore = true)
    @Mapping(target = "modifyTime", ignore = true)
    @Mapping(target = "logicDel", ignore = true)
    @Mapping(target = "recVer", ignore = true)
    @Mapping(target = "buildIn", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "deptId", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "userType", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "accountStatus", ignore = true)
    @Mapping(source = "nickName", target = "nickName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "telNo", target = "telNo")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "remark", target = "remark")
    void updateSysUserDto2Po(SysUserDto sysUserDto, @MappingTarget SysUserPo sysUserPo);
}
