package com.freesia.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.freesia.constant.Constants;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.UserModule;
import com.freesia.dto.OAuthUserInfo;
import com.freesia.dto.OAuthTokenResponse;
import com.freesia.dto.SysThirdpartyAuthDto;
import com.freesia.dto.SysUserDto;
import com.freesia.dto.OAuthLoginDto;
import com.freesia.exception.ServiceException;
import com.freesia.po.SysUserPo;
import com.freesia.properties.OAuthProperties;
import com.freesia.redis.util.URedis;
import com.freesia.satoken.constant.DeviceType;
import com.freesia.satoken.model.LoginUserModel;
import com.freesia.satoken.util.USecurity;
import com.freesia.service.*;
import com.freesia.strategy.auth.OAuthProvider;
import com.freesia.strategy.auth.OAuthProviderType;
import com.freesia.strategy.auth.impl.WechatOpenOAuthProvider;
import com.freesia.util.USpring;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Bliss.Wu
 * @Description OAuth 登录服务实现
 * @date 2026-05-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthLoginServiceImpl implements OAuthLoginService {
    private static final String OAUTH_STATE_PREFIX = "oauth:state:";
    private static final long STATE_TTL = 5; // 分钟

    private final OAuthProperties oAuthProperties;
    private final SysLoginService sysLoginService;
    private final SysUserService sysUserService;
    private final SysThirdpartyAuthService sysThirdpartyAuthService;

    @Override
    public String buildAuthorizeUrl(String provider, String redirectUrl) {
        OAuthProvider oauthProvider = getProvider(provider);
        String state = UUID.randomUUID().toString().replace("-", "");
        // 用 Redis 暂存 state → (provider + frontendRedirectUrl) 映射，5 分钟有效
        Map<String, String> stateData = new java.util.HashMap<>();
        stateData.put("provider", provider);
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            stateData.put("redirectUrl", redirectUrl);
        }
        URedis.set(OAUTH_STATE_PREFIX + state, stateData, STATE_TTL, TimeUnit.MINUTES);
        OAuthProviderType type = oauthProvider.getProviderType();
        OAuthProperties.OAuthProviderConfig config = oAuthProperties.getProviders().get(type.getCode());
        if (config == null) {
            throw new ServiceException(UserModule.SubModule.LOGIN, "oauth.config.not.found", new Object[]{provider});
        }
        return oauthProvider.buildAuthorizeUrl(state, config.getRedirectUri());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> handleCallback(String provider, String code, String state) {
        // 校验 state
        Object stateObj = URedis.get(OAUTH_STATE_PREFIX + state);
        if (stateObj == null) {
            throw new ServiceException(UserModule.SubModule.LOGIN, "oauth.state.invalid");
        }
        String frontendRedirectUrl = "/";
        if (stateObj instanceof Map) {
            Map<String, String> stateData = (Map<String, String>) stateObj;
            if (stateData.containsKey("redirectUrl")) {
                frontendRedirectUrl = stateData.get("redirectUrl");
            }
        }
        // 删除已使用的 state
        URedis.delete(OAUTH_STATE_PREFIX + state);

        OAuthProviderType type = OAuthProviderType.getInstanceByCode(provider);
        if (type == null) {
            throw new ServiceException(UserModule.SubModule.LOGIN, "oauth.provider.unsupported", new Object[]{provider});
        }
        OAuthProvider oauthProvider = getProvider(provider);
        OAuthProperties.OAuthProviderConfig config = oAuthProperties.getProviders().get(type.getCode());
        if (config == null) {
            throw new ServiceException(UserModule.SubModule.LOGIN, "oauth.config.not.found", new Object[]{provider});
        }

        // Step 1: 交换 access token
        OAuthTokenResponse tokenResp = oauthProvider.getAccessToken(code, state, config.getRedirectUri());

        // Step 2: 获取用户信息
        OAuthUserInfo userInfo;
        if (oauthProvider instanceof WechatOpenOAuthProvider) {
            userInfo = ((WechatOpenOAuthProvider) oauthProvider).getUserInfo(tokenResp.getAccessToken(), tokenResp.getOpenId());
        } else {
            userInfo = oauthProvider.getUserInfo(tokenResp.getAccessToken());
        }
        if (userInfo.getSource() == null) {
            userInfo.setSource(type.name());
        }

        // Step 3: 查找或创建用户（复用 wxLogin 模式）
        String generatedUsername = type.getCode() + "_" + userInfo.getOpenId();
        SysThirdpartyAuthDto queryDto = new SysThirdpartyAuthDto();
        queryDto.setOpenId(userInfo.getOpenId());
        queryDto.setSource(type.name());
        SysThirdpartyAuthDto existAuth = sysThirdpartyAuthService.findOne(queryDto);

        SysUserPo sysUserPo;
        if (ObjectUtil.isNull(existAuth)) {
            // 新用户：注册
            SysUserDto sysUserDto = new SysUserDto();
            sysUserDto.setUserName(generatedUsername);
            sysUserDto.setNickName(userInfo.getNickName());
            sysUserDto.setAvatar(userInfo.getAvatar());
            boolean registered = sysUserService.register(sysUserDto);
            if (!registered) {
                throw new ServiceException(UserModule.SubModule.LOGIN, "oauth.user.register.failed");
            }
            sysUserPo = sysUserService.findByUsername(generatedUsername);
        } else {
            String username = existAuth.getUserName();
            sysUserPo = sysUserService.findOneByUsername(username);
            if (ObjectUtil.isNull(sysUserPo)) {
                throw new ServiceException(UserModule.SubModule.LOGIN, "user.not.exists", new Object[]{username});
            }
        }

        // Step 4: 保存/更新 SYS_THIRDPARTY_AUTH 绑定
        saveOrUpdateThirdpartyAuth(userInfo, tokenResp, sysUserPo.getUserName(), existAuth);

        // Step 5: 登录
        LoginUserModel loginUserModel = sysLoginService.buildLoginUser(sysUserPo);
        USecurity.loginByDevice(loginUserModel, DeviceType.THIRD_PARTY_AUTH);

        String token = StpUtil.getTokenValue();
        Map<String, Object> result = new java.util.HashMap<>(4);
        result.put(Constants.TOKEN, token);
        result.put("redirectUrl", frontendRedirectUrl);
        return result;
    }

    @Override
    public Map<String, Object> oauthLogin(OAuthLoginDto dto) {
        // 直接调用回调逻辑（POST 模式，无 state 校验）
        return handleCallback(dto.getProvider(), dto.getCode(), dto.getState());
    }

    private OAuthProvider getProvider(String provider) {
        String beanName = provider + OAuthProvider.NAME;
        try {
            return USpring.getBean(beanName, OAuthProvider.class);
        } catch (Exception e) {
            throw new ServiceException(UserModule.SubModule.LOGIN, "oauth.provider.unsupported", new Object[]{provider});
        }
    }

    private void saveOrUpdateThirdpartyAuth(OAuthUserInfo userInfo, OAuthTokenResponse tokenResp,
                                             String username, SysThirdpartyAuthDto existAuth) {
        SysThirdpartyAuthDto authDto = new SysThirdpartyAuthDto();
        authDto.setAuthId(userInfo.getSource() + "_" + userInfo.getOpenId());
        authDto.setSource(userInfo.getSource());
        authDto.setOpenId(userInfo.getOpenId());
        authDto.setUnionId(tokenResp.getUnionId() != null ? tokenResp.getUnionId() : userInfo.getUnionId());
        authDto.setUserName(username);
        authDto.setNickName(userInfo.getNickName());
        authDto.setAvatar(userInfo.getAvatar());
        authDto.setAccessToken(tokenResp.getAccessToken());
        authDto.setRefreshToken(tokenResp.getRefreshToken());
        authDto.setTokenType(tokenResp.getTokenType());
        authDto.setScope(tokenResp.getScope());
        authDto.setEmail(userInfo.getEmail());
        if (ObjectUtil.isNull(existAuth)) {
            sysThirdpartyAuthService.saveUpdate(authDto);
        } else {
            authDto.setId(existAuth.getId());
            sysThirdpartyAuthService.saveUpdate(authDto);
        }
    }

    // ==================== 二维码扫码登录 ====================

    private static final String QRCODE_TICKET_PREFIX = "qrcode:ticket:";
    private static final long QRCODE_TTL = 5; // 分钟

    @Override
    public Map<String, Object> generateQrcodeTicket() {
        String ticket = UUID.randomUUID().toString().replace("-", "");
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("status", "pending");
        URedis.set(QRCODE_TICKET_PREFIX + ticket, data, QRCODE_TTL, TimeUnit.MINUTES);

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("ticket", ticket);
        result.put("expireIn", QRCODE_TTL * 60);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> checkQrcodeStatus(String ticket) {
        Object obj = URedis.get(QRCODE_TICKET_PREFIX + ticket);
        Map<String, Object> result = new java.util.HashMap<>();

        if (obj == null) {
            result.put("status", "expired");
            return result;
        }

        Map<String, Object> data = (Map<String, Object>) obj;
        String status = (String) data.get("status");
        result.put("status", status);

        if ("confirmed".equals(status)) {
            String loginId = (String) data.get("loginId");
            if (loginId != null) {
                StpUtil.login(loginId, SaLoginModel.create()
                        .setDevice("pc"));
                String token = StpUtil.getTokenValue();
                result.put("token", token);
            }
            URedis.delete(QRCODE_TICKET_PREFIX + ticket);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void bindQrcodeTicket(String ticket) {
        if (ticket == null || ticket.isEmpty()) {
            throw new ServiceException(UserModule.SubModule.LOGIN, "qrcode.ticket.invalid");
        }
        Object obj = URedis.get(QRCODE_TICKET_PREFIX + ticket);
        if (obj == null) {
            throw new ServiceException(UserModule.SubModule.LOGIN, "qrcode.ticket.expired");
        }
        Map<String, Object> data = (Map<String, Object>) obj;
        if (!"pending".equals(data.get("status"))) {
            throw new ServiceException(UserModule.SubModule.LOGIN, "qrcode.ticket.used");
        }
        String loginId = StpUtil.getLoginIdAsString();
        data.put("status", "confirmed");
        data.put("loginId", loginId);
        URedis.set(QRCODE_TICKET_PREFIX + ticket, data, 30, TimeUnit.SECONDS);
    }
}
