package com.freesia.controller;

import com.alibaba.fastjson.JSONObject;
import com.freesia.dto.SysMenuDto;
import com.freesia.entity.FindAllMenuTreeEntity;
import com.freesia.entity.FindMenuListByUserIdEntity;
import com.freesia.entity.FindTreeMenuSelectEntity;
import com.freesia.model.LoginUserModel;
import com.freesia.service.SysMenuService;
import com.freesia.util.UCopy;
import com.freesia.util.USecurity;
import com.freesia.vo.R;
import com.freesia.vo.SysMenuVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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
public class SysMenuController {
    @Resource(name = "sysMenuServiceImpl")
    private SysMenuService sysMenuService;

    /**
     * 批量保存菜单信息
     *
     * @param request {@link List<SysMenuVo>}
     * @return 形式返回
     */
    @Operation(summary = "批量保存菜单信息")
    @PostMapping(value = "saveMenuList")
    public R<List<SysMenuVo>> saveMenuList(@RequestBody String request) {
        List<SysMenuVo> sysMenuVoList = JSONObject.parseArray(request, SysMenuVo.class);
        List<SysMenuDto> sysMenuDtoList = sysMenuService.saveUpdateBatch(UCopy.fullCopyList(sysMenuVoList, SysMenuDto.class));
        return R.ok(UCopy.fullCopyList(sysMenuDtoList, SysMenuVo.class));
    }

    @Operation(summary = "查询所有菜单下拉树")
    @GetMapping(value = "findAllMenuTree")
    public R<List<FindAllMenuTreeEntity>> findAllMenuTree() {
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseGet(LoginUserModel::new);
        List<FindAllMenuTreeEntity> findAllMenuTreeEntityList = sysMenuService.findAllMenuTree(loginUser.getUserId());
        return R.ok(findAllMenuTreeEntityList);
    }

    @Operation(summary = "根据角色ID查询菜单列表")
    @GetMapping(value = "findSelectedMenuListByRoleId")
    public R<List<String>> findSelectedMenuListByRoleId(@NotNull String roleId) {
        List<Long> menuIdList = sysMenuService.findSelectedMenuListByRoleId(Long.valueOf(roleId));
        List<String> menuIdStrList = menuIdList.stream().map(Object::toString).collect(Collectors.toList());
        return R.ok(menuIdStrList);
    }

    @Operation(summary = "根据用户ID查询菜单列表")
    @GetMapping(value = "findMenuListByUserId")
    public R<List<FindMenuListByUserIdEntity>> findMenuListByUserId(SysMenuVo sysMenuVo) {
        SysMenuDto sysMenuDto = new SysMenuDto();
        UCopy.fullCopy(sysMenuVo, sysMenuDto);
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseGet(LoginUserModel::new);
        List<FindMenuListByUserIdEntity> menuList = sysMenuService.findMenuListByUserId(sysMenuDto, loginUser.getUserId());
        return R.ok(menuList);
    }

    @Operation(summary = "查询菜单树下拉框集合")
    @GetMapping(value = "findTreeMenuSelect")
    public R<List<FindTreeMenuSelectEntity>> findTreeMenuSelect(@RequestParam String menuType) {
        LoginUserModel loginUser = Optional.ofNullable(USecurity.getLoginUser()).orElseGet(LoginUserModel::new);
        List<FindTreeMenuSelectEntity> menuList = sysMenuService.findTreeMenuSelect(loginUser.getUserId(), menuType);
        return R.ok(menuList);
    }

    @Operation(summary = "保存目录-菜单-按钮、链接")
    @PostMapping(value = "saveMenu")
    public R<SysMenuDto> saveMenu(@RequestBody @Valid SysMenuVo sysMenuVo) {
        SysMenuDto sysMenuDto = UCopy.copyVo2Dto(sysMenuVo, SysMenuDto.class);
        sysMenuDto = sysMenuService.saveMenu(sysMenuDto);
        return R.ok(sysMenuDto);
    }

    @Operation(summary = "删除目录、菜单、按钮、链接")
    @DeleteMapping(value = "deleteMenu")
    public R<SysMenuDto> deleteMenu(@RequestParam Long id) {
        sysMenuService.deleteMenu(id);
        return R.ok();
    }
}
