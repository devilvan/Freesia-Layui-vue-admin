package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.freesia.constant.MenuPermission;
import com.freesia.dto.SysRoleDto;
import com.freesia.entity.FindAllRolesEntity;
import com.freesia.entity.FindPageSysRoleListEntity;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.SysRoleService;
import com.freesia.util.UCopy;
import com.freesia.vo.AssignRoleVo;
import com.freesia.vo.R;
import com.freesia.vo.SaveRoleMenuPrivilegeVo;
import com.freesia.vo.SysRoleVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
        SysRoleDto sysRoleDto = new SysRoleDto();
        UCopy.fullCopy(sysRoleVo, sysRoleDto);
        return sysRoleService.findPageSysRoleList(sysRoleDto, pageQuery);
    }

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
    @GetMapping("findAllRoles")
    public R<List<FindAllRolesEntity>> findAllRoles() {
        List<FindAllRolesEntity> findAllRolesEntityList = sysRoleService.findAllRoles();
        return R.ok(findAllRolesEntityList);
    }
}
