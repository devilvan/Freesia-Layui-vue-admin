package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.freesia.constant.Constants;
import com.freesia.constant.SysConfigConstant;
import com.freesia.crypt.util.UCrypt;
import com.freesia.dto.*;
import com.freesia.entity.RouterEntity;
import com.freesia.entity.SysUserEntity;
import com.freesia.entity.SysUserInfoEntity;
import com.freesia.satoken.model.LoginUserModel;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.*;
import com.freesia.util.UCollection;
import com.freesia.util.UCopy;
import com.freesia.util.UMessage;
import com.freesia.vo.LoginVo;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
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
public class SysLoginController extends BaseController {
    private final SysLoginService sysLoginService;
    private final SysUserService sysUserService;
    private final SysMenuService sysMenuService;
    private final SysTenantService sysTenantService;
    private final SysConfigService sysConfigService;
    private final OAuthLoginService oAuthLoginService;

    // ==================== OAuth 单点登录 ====================

    @SaIgnore
    @Operation(summary = "OAuth 授权跳转（重定向到第三方授权页）")
    @GetMapping("oauth/authorize/{provider}")
    public void oauthAuthorize(@PathVariable String provider,
                               @RequestParam(required = false) String redirectUrl,
                               javax.servlet.http.HttpServletResponse response) {
        String authorizeUrl = oAuthLoginService.buildAuthorizeUrl(provider, redirectUrl);
        try {
            response.sendRedirect(authorizeUrl);
        } catch (Exception e) {
            throw new RuntimeException("OAuth 授权跳转失败: " + provider, e);
        }
    }

    @SaIgnore
    @Operation(summary = "OAuth 回调（第三方授权后回调到后端，完成登录后重定向到前端回调页）")
    @GetMapping("oauth/callback/{provider}")
    public void oauthCallback(@PathVariable String provider,
                              @RequestParam String code,
                              @RequestParam String state,
                              javax.servlet.http.HttpServletResponse response) {
        Map<String, Object> result = oAuthLoginService.handleCallback(provider, code, state);
        String token = (String) result.get(Constants.TOKEN);
        String frontendRedirectUrl = (String) result.getOrDefault("redirectUrl", "/");
        try {
            // 重定向到前端回调页，token 通过 URL 参数传递
            String redirectTo = frontendRedirectUrl;
            if (redirectTo.contains("?")) {
                redirectTo += "&token=" + token;
            } else {
                redirectTo += "?token=" + token;
            }
            response.sendRedirect(redirectTo);
        } catch (Exception e) {
            throw new RuntimeException("OAuth 回调处理失败", e);
        }
    }

    @SaIgnore
    @Operation(summary = "OAuth 统一登录（POST 模式，适用于小程序等）")
    @PostMapping("oauthLogin")
    public R<Map<String, Object>> oauthLogin(@Valid @RequestBody OAuthLoginDto dto) {
        Map<String, Object> result = oAuthLoginService.oauthLogin(dto);
        return R.ok(result);
    }

    // ==================== 二维码扫码登录 ====================

    @SaIgnore
    @Operation(summary = "生成扫码登录二维码 ticket")
    @GetMapping("qrcode/generate")
    public R<Map<String, Object>> qrcodeGenerate() {
        Map<String, Object> result = oAuthLoginService.generateQrcodeTicket();
        return R.ok(result);
    }

    @SaIgnore
    @Operation(summary = "轮询扫码登录二维码状态")
    @GetMapping("qrcode/status/{ticket}")
    public R<Map<String, Object>> qrcodeStatus(@PathVariable String ticket) {
        Map<String, Object> result = oAuthLoginService.checkQrcodeStatus(ticket);
        return R.ok(result);
    }

    @Operation(summary = "小程序扫码绑定 ticket 到当前用户（需已登录）")
    @PostMapping("qrcode/bind")
    public R<Void> qrcodeBind(@RequestBody Map<String, String> body) {
        String ticket = body.get("ticket");
        oAuthLoginService.bindQrcodeTicket(ticket);
        return R.ok();
    }

    // ==================== 原有登录接口 ====================

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
    @Operation(summary = "微信登录")
    @PostMapping("wxLogin")
    public R<Map<String, Object>> wxLogin(@Valid @RequestBody WxLoginDto wxLoginDto) {
        Map<String, Object> result = sysLoginService.wxLogin(wxLoginDto);
        return R.ok(result);
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

    @SaIgnore
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

    @SaIgnore
    @Operation(summary = "查询验证码启用状态")
    @GetMapping(value = "findCaptchaEnabled")
    public R<Boolean> findCaptchaEnabled() {
        SysConfigDto sysConfigDto = sysConfigService.findConfigByKey(SysConfigConstant.SYS_ACCOUNT_CAPTCHA_ENABLED);
        String captchaEnabled = sysConfigDto.getConfigValue();
        boolean captchaEnabledFlag = Convert.toBool(captchaEnabled, false);
        return R.ok(captchaEnabledFlag);
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
