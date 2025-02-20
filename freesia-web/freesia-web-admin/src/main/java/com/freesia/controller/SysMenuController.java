package com.freesia.controller;

import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.alibaba.fastjson.JSONObject;
import com.freesia.constant.MenuModule;
import com.freesia.constant.MenuPermission;
import com.freesia.constant.MenuType;
import com.freesia.dto.AssignButtonDto;
import com.freesia.dto.SysMenuDto;
import com.freesia.entity.FindAllMenuTreeEntity;
import com.freesia.entity.FindMenuListByUserIdEntity;
import com.freesia.entity.FindTreeMenuSelectEntity;
import com.freesia.exception.ServiceException;
import com.freesia.exception.UserException;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.satoken.model.LoginUserModel;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.SysMenuService;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.vo.AssignButtonVo;
import com.freesia.vo.R;
import com.freesia.vo.SysMenuVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 目录/菜单/按钮信息表 控制器
 * @date 2023-08-24
 */
@Valid
@RestController
@RequestMapping(value = "/api/sysMenuController")
@Tag(name = "SysMenuController", description = "目录-菜单-按钮信息表 控制器")
public class SysMenuController extends BaseController {
    @Resource(name = "sysMenuServiceImpl")
    private SysMenuService sysMenuService;

    /**
     * 批量保存菜单信息
     *
     * @param request {@link List<SysMenuVo>}
     * @return 形式返回
     */
    @Idempotent
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_EDIT),
            @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_ADD_DIR),
            @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_ADD_MENU),
            @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_ADD_BUTTON),
            @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_ADD_LINK)
    })
    @Operation(summary = "批量保存菜单信息")
    @PostMapping(value = "saveMenuList")
    public R<List<SysMenuVo>> saveMenuList(@RequestBody String request) {
        List<SysMenuVo> sysMenuVoList = JSONObject.parseArray(request, SysMenuVo.class);
        List<SysMenuDto> sysMenuDtoList = sysMenuService.saveUpdateBatch(UCopy.fullCopyList(sysMenuVoList, SysMenuDto.class));
        return R.ok(UCopy.fullCopyList(sysMenuDtoList, SysMenuVo.class));
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_INDEX)
    @Operation(summary = "查询所有菜单下拉树")
    @GetMapping(value = "findAllMenuTree")
    public R<List<FindAllMenuTreeEntity>> findAllMenuTree() {
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseThrow(() -> new UserException("user.info.null", new Object[] {}));
        List<FindAllMenuTreeEntity> findAllMenuTreeEntityList = sysMenuService.findAllMenuTree(loginUser.getUserId());
        return R.ok(findAllMenuTreeEntityList);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_INDEX)
    @Operation(summary = "根据角色ID查询所有菜单下拉树")
    @GetMapping(value = "findAllMenuTreeByRoleId")
    public R<List<FindAllMenuTreeEntity>> findAllMenuTree(@RequestParam Long roleId) {
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseThrow(() -> new UserException("user.info.null", new Object[] {}));
        List<FindAllMenuTreeEntity> findAllMenuTreeEntityList = sysMenuService.findAllMenuTree(roleId, loginUser.getUserId());
        return R.ok(findAllMenuTreeEntityList);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_ROLE_MENU_EDIT)
    @Operation(summary = "根据角色ID查询菜单列表")
    @GetMapping(value = "findSelectedMenuListByRoleId")
    public R<List<String>> findSelectedMenuListByRoleId(@NotNull String roleId) {
        List<Long> menuIdList = sysMenuService.findSelectedMenuListByRoleId(Long.valueOf(roleId));
        List<String> menuIdStrList = menuIdList.stream().map(Object::toString).collect(Collectors.toList());
        return R.ok(menuIdStrList);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_INDEX)
    @Operation(summary = "根据用户ID查询菜单列表")
    @GetMapping(value = "findMenuListByUserId")
    public R<List<FindMenuListByUserIdEntity>> findMenuListByUserId(SysMenuVo sysMenuVo) {
        SysMenuDto sysMenuDto = UCopy.copyVo2Dto(sysMenuVo, SysMenuDto.class);
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseGet(LoginUserModel::new);
        List<FindMenuListByUserIdEntity> menuList = sysMenuService.findMenuListByUserId(sysMenuDto, loginUser.getUserId());
        return R.ok(menuList);
    }

    @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_INDEX)
    @Operation(summary = "查询菜单树下拉框集合")
    @GetMapping(value = "findTreeMenuSelect")
    public R<List<FindTreeMenuSelectEntity>> findTreeMenuSelect(@RequestParam String menuType) {
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseGet(LoginUserModel::new);
        List<FindTreeMenuSelectEntity> menuList = sysMenuService.findTreeMenuSelect(loginUser.getUserId(), menuType);
        return R.ok(menuList);
    }

    @Idempotent
    @SaCheckOr(permission = {
            @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_EDIT),
            @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_ADD_DIR),
            @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_ADD_MENU),
            @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_ADD_BUTTON),
            @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_ADD_LINK)
    })
    @Operation(summary = "保存目录-菜单-按钮、链接")
    @PostMapping(value = "saveMenu")
    public R<SysMenuDto> saveMenu(@RequestBody @Valid SysMenuVo sysMenuVo) {
        SysMenuDto sysMenuDto = UCopy.copyVo2Dto(sysMenuVo, SysMenuDto.class);
        String path = sysMenuDto.getPath();
        if (!MenuType.BUTTON.getType().equals(sysMenuDto.getMenuType())) {
            sysMenuDto.setPath(path.trim());
        }
        if (UEmpty.isEmpty(path)) {
            sysMenuDto.setPath(null);
        }
        sysMenuDto = sysMenuService.saveMenu(sysMenuDto);
        return R.ok(sysMenuDto);
    }

    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_MENU_DELETE)
    @Operation(summary = "删除目录、菜单、按钮、链接")
    @DeleteMapping(value = "deleteMenu")
    public R<SysMenuDto> deleteMenu(@RequestParam Long id) {
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser())
                .orElseThrow(() -> new ServiceException(MenuModule.MENU_MANAGEMENT, "user.info.null"));
        sysMenuService.deleteMenu(id, loginUser.getUserId());
        return R.ok();
    }

    @Operation(summary = "查询菜单下所有的按钮")
    @GetMapping(value = "findAllSysButton")
    public R<List<SysMenuDto>> findAllSysButton(SysMenuVo sysMenuVo, @Validated @NotEmpty String roleId) {
        SysMenuDto sysMenuDto = UCopy.copyVo2Dto(sysMenuVo, SysMenuDto.class);
        sysMenuDto.setRoleId(Long.valueOf(roleId));
        List<SysMenuDto> sysMenuDtoList = sysMenuService.findAllSysButton(sysMenuDto);
        return R.ok(sysMenuDtoList);
    }

    @Operation(summary = "根据角色ID查询菜单下已分配的按钮ID")
    @GetMapping(value = "findAssignedSysButtonByRoleId")
    public R<List<Long>> findAssignedSysButtonByRoleId(SysMenuVo sysMenuVo, @Validated @NotEmpty String roleId) {
        SysMenuDto sysMenuDto = UCopy.copyVo2Dto(sysMenuVo, SysMenuDto.class);
        List<Long> buttonIdList = sysMenuService.findAssignedSysButtonByRoleId(sysMenuDto, Long.valueOf(roleId));
        return R.ok(buttonIdList);
    }

    @Idempotent
    @SaCheckPermission(value = MenuPermission.SYSTEM_ROLE_ASSIGN_BUTTON_EDIT)
    @Operation(summary = "分配按钮")
    @PostMapping(value = "assignButton")
    public R<Void> assignButton(@RequestBody @Valid AssignButtonVo assignButtonVo) {
        AssignButtonDto assignButtonDto = AssignButtonVo.convertVo2Dto(assignButtonVo);
        sysMenuService.assignButton(assignButtonDto);
        return R.ok();
    }

    @Operation(summary = "查询自增排序号")
    @GetMapping(value = "findIncrementOrderNum")
    public R<Long> findIncrementOrderNum(@RequestParam("menuId") String menuId) {
        SysMenuDto sysMenuDto = new SysMenuDto();
        sysMenuDto.setId(Long.valueOf(menuId));
        Long incrementOrderNum = sysMenuService.findIncrementOrderNum(sysMenuDto);
        return R.ok(incrementOrderNum);
    }
}
