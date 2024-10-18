package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.MenuPermission;
import com.freesia.dto.SysRoleDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.FindAllRolesEntity;
import com.freesia.entity.FindDeptRolesByRoleIdEntity;
import com.freesia.entity.FindPageSysRoleListEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysRoleService;
import com.freesia.util.UCopy;
import com.freesia.satoken.util.USecurity;
import com.freesia.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 角色信息表 控制器
 * @date 2023-09-01
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysRoleController")
@Tag(name = "SysRoleController", description = "角色信息表 控制器")
public class SysRoleController {
    private final SysRoleService sysRoleService;

    @Operation(summary = "获取角色列表分页")
    @GetMapping("findPageSysRoleList")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_ROLE_INDEX})
    public TableResult<FindPageSysRoleListEntity> findPageSysRoleList(SysRoleVo sysRoleVo, PageQuery pageQuery) {
        SysRoleDto sysRoleDto = UCopy.copyVo2Dto(sysRoleVo, SysRoleDto.class);
        return sysRoleService.findPageSysRoleList(sysRoleDto, pageQuery);
    }

    @Idempotent
    @Operation(summary = "保存角色对应的菜单信息")
    @PostMapping(value = "saveRoleMenuPrivilege")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_ROLE_MENU_EDIT})
    public R<Void> saveRoleMenuPrivilege(@RequestBody SaveRoleMenuPrivilegeVo saveRoleMenuPrivilegeVo) {
        Long roleId = saveRoleMenuPrivilegeVo.getRoleId();
        String dataScope = saveRoleMenuPrivilegeVo.getDataScope();
        List<Long> treeSelectedIdList = saveRoleMenuPrivilegeVo.getTreeSelectedIdList();
        sysRoleService.saveRoleMenuPrivilege(treeSelectedIdList, roleId, dataScope);
        return R.ok();
    }

    @Operation(summary = "查询所有角色")
    @GetMapping(value = "findAllRoles")
    public R<List<FindAllRolesEntity>> findAllRoles() {
        List<FindAllRolesEntity> findAllRolesEntityList = sysRoleService.findAllRoles();
        return R.ok(findAllRolesEntityList);
    }

    @Operation(summary = "根据ID查询角色")
    @GetMapping(value = "findRoleById")
    public R<SysRoleDto> findRoleById(@RequestParam Long roleId) {
        SysRoleDto sysRoleDto = sysRoleService.findRoleById(roleId);
        return R.ok(sysRoleDto);
    }

    @Operation(summary = "查询用户信息和已分配该角色的用户列表")
    @GetMapping(value = "findPageUserByRoleId")
    public TableResult<SysUserDto> findPageUserByRoleId(SysRoleVo sysRoleVo, PageQuery pageQuery) {
        SysRoleDto sysRoleDto = UCopy.copyVo2Dto(sysRoleVo, SysRoleDto.class);
        sysRoleDto.setTenantId(USecurity.getTenantId());
        return sysRoleService.findPageUserByRoleId(sysRoleDto, pageQuery);
    }

    @Operation(summary = "查询未分配该角色的用户列表")
    @GetMapping(value = "findPageAllowAssignUserByRoleId")
    public TableResult<SysUserDto> findPageAllowAssignUserByRoleId(SysRoleVo sysRoleVo, PageQuery pageQuery) {
        SysRoleDto sysRoleDto = UCopy.copyVo2Dto(sysRoleVo, SysRoleDto.class);
        sysRoleDto.setTenantId(USecurity.getTenantId());
        return sysRoleService.findPageAllowAssignUserByRoleId(sysRoleDto, pageQuery);
    }

    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_ROLE_ASSIGN_USER_EDIT)
    @Operation(summary = "分配用户")
    @PostMapping(value = "assignUser")
    public R<Void> assignUser(@RequestBody @Validated AssignUserVo assignUserVo) {
        Long roleId = Long.parseLong(assignUserVo.getRoleId());
        List<Long> userIdList = assignUserVo.getUserIdList();
        sysRoleService.assignUser(roleId, userIdList);
        return R.ok();
    }

    @Idempotent
    @Operation(summary = "取消分配用户")
    @PostMapping(value = "cancelAssignUser")
    public R<Void> cancelAssignUser(@RequestBody @Validated AssignUserVo assignUserVo) {
        Long roleId = Long.parseLong(assignUserVo.getRoleId());
        List<Long> userIdList = assignUserVo.getUserIdList();
        sysRoleService.cancelAssignUser(roleId, userIdList);
        return R.ok();
    }

    @Idempotent
    @Operation(summary = "给角色分配部门")
    @PostMapping("assignDept")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_ROLE_ASSIGN_DEPT})
    public R<Void> assignDept(@RequestBody RoleAssignDeptVo assignDeptVo) {
        Set<Long> deptIdSet = assignDeptVo.getDeptIdList();
        Long roleId = assignDeptVo.getRoleId();
        sysRoleService.assignDept(roleId, deptIdSet);
        return R.ok();
    }

    @Operation(summary = "根据角色ID查询【分配部门】加载数据")
    @GetMapping("findDeptRolesByRoleId")
    @SaCheckPermission(value = {MenuPermission.SYSTEM_ROLE_ASSIGN_DEPT})
    public R<FindDeptRolesByRoleIdEntity> findDeptRolesByRoleId(@RequestParam Long roleId) {
        FindDeptRolesByRoleIdEntity findDeptRolesByDeptIdEntity = sysRoleService.findDeptRolesByRoleId(roleId);
        return R.ok(findDeptRolesByDeptIdEntity);
    }

    @Idempotent
    @Operation(summary = "保存角色")
    @PostMapping("saveRole")
    @SaCheckOr(permission = {
            @SaCheckPermission(value = {MenuPermission.SYSTEM_ROLE_ADD}),
            @SaCheckPermission(value = {MenuPermission.SYSTEM_ROLE_EDIT})
    })
    public R<SysRoleDto> saveRole(@RequestBody SaveRoleVo saveRoleVo) {
        String status = saveRoleVo.getStatus();
        saveRoleVo.setStatus(FlagConstant.TRUE.equals(status) ? FlagConstant.ENABLED : FlagConstant.DISABLED);
        SysRoleDto sysRoleDto = UCopy.copyVo2Dto(saveRoleVo, SysRoleDto.class);
        sysRoleDto = sysRoleService.saveRole(sysRoleDto);
        return R.ok(sysRoleDto);
    }

    @Idempotent
    @Operation(summary = "删除角色")
    @PostMapping("deleteRole")
    @SaCheckPermission(value = MenuPermission.SYSTEM_ROLE_DELETE)
    public R<Void> deleteRole(@RequestBody SysRoleVo sysRoleVo) {
        SysRoleDto sysRoleDto = UCopy.copyVo2Dto(sysRoleVo, SysRoleDto.class);
        sysRoleService.deleteRole(sysRoleDto);
        return R.ok();
    }

}
