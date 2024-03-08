package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.freesia.constant.MenuPermission;
import com.freesia.dto.SysTenantDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindPageSysUserByDeptEntity;
import com.freesia.entity.FindPageSysUserListEntity;
import com.freesia.entity.FindUserRolesByUserIdEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysUserService;
import com.freesia.util.UCopy;
import com.freesia.util.USecurity;
import com.freesia.vo.AssignRoleVo;
import com.freesia.vo.R;
import com.freesia.vo.SysTenantVo;
import com.freesia.vo.SysUserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 用户管理 控制器
 * @date 2023-08-30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysUserController")
@Tag(name = "SysUserController", description = "用户信息表 控制器")
public class SysUserController {
    private final SysUserService sysUserService;

    @Operation(summary = "获取用户列表分页")
    @GetMapping("findPageSysUserList")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_USER_INDEX})
    public TableResult<FindPageSysUserListEntity> findPageSysUserList(SysUserVo sysUserVo, PageQuery pageQuery) {
        SysUserDto sysUserDto = new SysUserDto();
        UCopy.fullCopy(sysUserVo, sysUserDto);
        return sysUserService.findPageSysUserList(sysUserDto, pageQuery);
    }

    @Operation(summary = "获取部门下的用户")
    @GetMapping("findPageSysUserByDept")
    public TableResult<FindPageSysUserByDeptEntity> findPageSysUserByDept(SysUserVo sysUserVo, PageQuery pageQuery) {
        SysUserDto sysUserDto = new SysUserDto();
        UCopy.fullCopy(sysUserVo, sysUserDto);
        sysUserDto.setTenantId(USecurity.getTenantId());
        return sysUserService.findPageSysUserByDept(sysUserDto, pageQuery);
    }

    @Operation(summary = "查询用户信息")
    @GetMapping("findCurrentUserProfile")
    public R<SysUserDto> findCurrentUserProfile() {
        SysUserDto sysUserDto = sysUserService.findCurrentUserProfile(USecurity.getUserId());
        return R.ok(sysUserDto);
    }

    @Operation(summary = "修改用户信息")
    @PutMapping("saveUserInfo")
    public R<Void> saveUserInfo(@RequestBody SysUserVo sysUserVo) {
        SysUserDto sysUserDto = UCopy.copyVo2Dto(sysUserVo, SysUserDto.class);
        sysUserService.saveUserInfo(sysUserDto);
        return R.ok();
    }

    @Operation(summary = "根据用户ID查询【分配用户】加载数据")
    @GetMapping("findUserRolesByUserId")
    public R<FindUserRolesByUserIdEntity> findUserRolesByUserId(@RequestParam Long userId) {
        FindUserRolesByUserIdEntity findUserRolesByUserIdEntity = sysUserService.findUserRolesByUserId(userId);
        return R.ok(findUserRolesByUserIdEntity);
    }

    @Operation(summary = "给用户分配角色")
    @PostMapping("assignRole")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_USER_ASSIGN_ROLE})
    public R<Void> assignRole(@RequestBody AssignRoleVo assignRoleVo) {
        Long userId = assignRoleVo.getUserId();
        Set<Long> afterRoleIdSet = assignRoleVo.getAfterRoleIdSet();
        sysUserService.assignRole(userId, afterRoleIdSet);
        return R.ok();
    }

    @Operation(summary = "根据租户ID查询已分配该租户的用户")
    @GetMapping("findPageUserByTenantId")
    public TableResult<SysUserDto> findPageUserByTenantId(SysTenantVo sysTenantVo, PageQuery pageQuery) {
        return sysUserService.findPageUserByTenantId(sysTenantVo.getId(), pageQuery);
    }

    @Operation(summary = "根据租户ID查询可分配该租户的用户")
    @GetMapping("findPageAllowAssignUserByTenantId")
    public TableResult<SysUserDto> findPageAllowAssignUserByTenantId(SysTenantVo sysTenantVo, PageQuery pageQuery) {
        SysTenantDto sysTenantDto = UCopy.copyVo2Dto(sysTenantVo, SysTenantDto.class);
        return sysUserService.findPageAllowAssignUserByTenantId(sysTenantDto, pageQuery);
    }
}
