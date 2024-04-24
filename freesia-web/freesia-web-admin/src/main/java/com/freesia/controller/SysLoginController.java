package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.freesia.constant.Constants;
import com.freesia.dto.RouterDto;
import com.freesia.dto.SysMenuDto;
import com.freesia.dto.SysTenantDto;
import com.freesia.dto.SysUserDto;
import com.freesia.entity.RouterEntity;
import com.freesia.entity.SysUserEntity;
import com.freesia.entity.SysUserInfoEntity;
import com.freesia.model.LoginUserModel;
import com.freesia.service.SysLoginService;
import com.freesia.service.SysMenuService;
import com.freesia.service.SysTenantService;
import com.freesia.service.SysUserService;
import com.freesia.util.*;
import com.freesia.vo.LoginVo;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author Evad.Wu
 * @Description 登录功能 控制器
 * @date 2023-08-11
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysLoginController")
@Tag(name = "SysLoginController", description = "登录功能 控制器")
public class SysLoginController {
    private final SysLoginService sysLoginService;
    private final SysUserService sysUserService;
    private final SysMenuService sysMenuService;
    private final SysTenantService sysTenantService;

    @SaIgnore
    @Operation(summary = "客户端登录")
    @PostMapping("sysLogin")
    public R<Map<String, Object>> sysLogin(@Valid @RequestBody String request) {
        LoginVo loginVo = UCrypt.aesDecryptJSON(request, LoginVo.class);
        Map<String, Object> ajax = UCollection.optimizeInitialCapacityMap(1, UCollection.LOAD_FACTOR);
        // 生成令牌
        String token = sysLoginService.login(loginVo.getUsername(), loginVo.getPassword(), loginVo.getCode(), loginVo.getCaptchaKey());
        ajax.put(Constants.TOKEN, token);
        return R.ok(ajax);
    }

    @SaIgnore
    @Operation(summary = "客户端登出")
    @PostMapping("sysLogOut")
    public R<Void> sysLogOut() {
        sysLoginService.logout();
        return R.ok();
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("getInfo")
    public R<SysUserInfoEntity> getInfo() {
        LoginUserModel loginUserModel = USecurity.getLoginUser();
        if (ObjectUtil.isNull(loginUserModel)) {
            return R.ok(HttpStatus.HTTP_BAD_REQUEST, UMessage.message("user.info.null"));
        }
        SysUserDto sysUserDto = sysUserService.findUserById(loginUserModel.getUserId());
        SysUserInfoEntity sysUserInfoEntity = sysUserDto2Entity(sysUserDto, loginUserModel);
        List<SysTenantDto> sysTenantPoList = sysTenantService.findListSysTenantByUserId(loginUserModel.getUserId());
        sysUserInfoEntity.setSysTenantDtoList(sysTenantPoList);
        return R.ok(sysUserInfoEntity);
    }

    @Operation(summary = "获取路由信息")
    @GetMapping("getRouters")
    public R<List<RouterDto>> getRouters() {
        Long userId = USecurity.getUserId();
        List<SysMenuDto> sysMenuDtoList = sysMenuService.findMenuTreeByUserId(userId);
        List<RouterDto> routerDtoList = sysMenuService.buildRouters(sysMenuDtoList);
        return R.ok(routerDtoList);
    }

    @Operation(summary = "获取侧边栏菜单信息")
    @GetMapping("getMenu")
    public R<List<RouterEntity>> getMenu() {
        Long userId = USecurity.getUserId();
        List<SysMenuDto> sysMenuDtoList = sysMenuService.findMenuTreeByUserId(userId);
        List<RouterEntity> routerEntityList = sysMenuService.buildMenus(sysMenuDtoList);
        return R.ok(routerEntityList);
    }

    /**
     * SysUserDto转Entity
     *
     * @param sysUserDto     数据传输对象
     * @param loginUserModel 用户信息
     * @return 值对象
     */
    private SysUserInfoEntity sysUserDto2Entity(SysUserDto sysUserDto, LoginUserModel loginUserModel) {
        SysUserInfoEntity sysUserInfoEntity = new SysUserInfoEntity();
        SysUserEntity sysUserEntity = new SysUserEntity();
        UCopy.fullCopy(sysUserDto, sysUserEntity);
        sysUserInfoEntity.setUser(sysUserEntity);
        sysUserInfoEntity.setRoles(loginUserModel.getRolePermission());
        sysUserInfoEntity.setPermissions(loginUserModel.getMenuPermission());
        return sysUserInfoEntity;
    }
}
