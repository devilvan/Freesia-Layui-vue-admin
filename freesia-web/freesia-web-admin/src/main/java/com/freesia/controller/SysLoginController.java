package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
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
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import com.freesia.vo.LoginVo;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
    private final SysThirdpartyAuthService sysThirdpartyAuthService;

    // ==================== OAuth 单点登录 ====================

    @SaIgnore
    @Operation(summary = "OAuth 授权跳转（重定向到第三方授权页）")
    @GetMapping("oauth/authorize/{provider}")
    public void oauthAuthorize(@PathVariable String provider,
                               @RequestParam(required = false) String redirectUrl,
                               jakarta.servlet.http.HttpServletResponse response) {
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
                              @RequestParam(required = false) String state,
                              jakarta.servlet.http.HttpServletResponse response) {
        Map<String, Object> result = oAuthLoginService.handleCallback(provider, code, state);
        String token = (String) result.get(Constants.TOKEN);
        String frontendRedirectUrl = (String) result.getOrDefault("redirectUrl", "/");
        try {
            // 将 token 放在 hash 之前，hash 路由模式下才能读取到
            String redirectTo;
            int hashIdx = frontendRedirectUrl.indexOf('#');
            if (hashIdx >= 0) {
                redirectTo = frontendRedirectUrl.substring(0, hashIdx)
                        + "?token=" + token
                        + frontendRedirectUrl.substring(hashIdx);
            } else if (frontendRedirectUrl.contains("?")) {
                redirectTo = frontendRedirectUrl + "&token=" + token;
            } else {
                redirectTo = frontendRedirectUrl + "?token=" + token;
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

    // ==================== 二维码扫码登录 ====================1

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
        return buildLoginResult(request);
    }

    @SaIgnore
    @Operation(summary = "邮箱登录")
    @PostMapping("emailLogin")
    public R<Map<String, Object>> emailLogin(@Valid @RequestBody String request) {
        return buildLoginResult(request);
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

    @Operation(summary = "续期Token，使用当前有效token换取新token以延长有效期")
    @PostMapping("renewToken")
    public R<Map<String, Object>> renewToken() {
        String loginId = StpUtil.getLoginIdAsString();
        LoginUserModel loginUser = USecurity.getLoginUser();
        // 重新登录生成新JWT，is-share=true 时共享同一session，不会丢失用户信息
        StpUtil.login(loginId);
        // 确保新token session中有完整的用户信息
        if (loginUser != null) {
            StpUtil.getTokenSession().set(USecurity.LOGIN_USER_KEY, loginUser);
        }
        String newToken = StpUtil.getTokenValue();
        Map<String, Object> result = UCollection.optimizeInitialCapacityMap(1, UCollection.LOAD_FACTOR);
        result.put(Constants.TOKEN, newToken);
        return R.ok(result);
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

        // 查询第三方平台授权绑定列表（含头像、邮箱、昵称等第三方平台信息）
        SysThirdpartyAuthDto queryAuth = new SysThirdpartyAuthDto();
        queryAuth.setUserName(loginUserModel.getUsername());
        List<SysThirdpartyAuthDto> authList = sysThirdpartyAuthService.findList(queryAuth);
        sysUserInfoEntity.setSysThirdpartyAuthList(authList);

        // 如果用户实体中缺少头像、邮箱、昵称，则从第三方授权记录中补充
        enrichUserFromThirdpartyAuth(sysUserInfoEntity.getUser(), authList);

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

    /**
     * 当用户实体中缺少头像、邮箱、昵称时，从第三方授权记录中补充
     * <p>
     * 第三方登录（Gitee/GitHub/微信等）注册时虽然会把头像等写入 SYS_USER 表，
     * 但后续第三方平台更新信息时只会更新 SYS_THIRDPARTY_AUTH 表。
     * 此方法确保 getInfo 始终返回最新的第三方用户信息。
     *
     * @param userEntity 用户实体（会被直接修改）
     * @param authList   第三方授权绑定列表
     */
    private void enrichUserFromThirdpartyAuth(SysUserEntity userEntity,
                                               List<SysThirdpartyAuthDto> authList) {
        if (userEntity == null || UEmpty.isEmpty(authList)) {
            return;
        }
        for (SysThirdpartyAuthDto auth : authList) {
            if (UEmpty.isEmpty(userEntity.getAvatar()) && UEmpty.isNotEmpty(auth.getAvatar())) {
                userEntity.setAvatar(auth.getAvatar());
            }
            if (UEmpty.isEmpty(userEntity.getEmail()) && UEmpty.isNotEmpty(auth.getEmail())) {
                userEntity.setEmail(auth.getEmail());
            }
            if (UEmpty.isEmpty(userEntity.getNickName()) && UEmpty.isNotEmpty(auth.getNickName())) {
                userEntity.setNickName(auth.getNickName());
            }
        }
    }

    private R<Map<String, Object>> buildLoginResult(String request) {
        LoginVo loginVo = UCrypt.aesDecryptJSON(request, LoginVo.class);
        Map<String, Object> ajax = UCollection.optimizeInitialCapacityMap(1, UCollection.LOAD_FACTOR);
        String token = sysLoginService.login(loginVo.getUsername(), loginVo.getPassword(), loginVo.getCode(), loginVo.getCaptchaKey());
        ajax.put(Constants.TOKEN, token);
        return R.ok(ajax);
    }
}
