package com.freesia.strategy.auth.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.freesia.dto.OAuthTokenResponse;
import com.freesia.dto.OAuthUserInfo;
import com.freesia.net.builder.HttpBuilder;
import com.freesia.net.component.HttpClientComponent;
import com.freesia.net.dto.HttpClientDto;
import com.freesia.properties.OAuthProperties;
import com.freesia.strategy.auth.OAuthProvider;
import com.freesia.strategy.auth.OAuthProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bliss.Wu
 * @Description 微信开放平台 OAuth 2.0 Provider（PC 扫码登录）
 * @date 2026-05-30
 */
@Component(WechatOpenOAuthProvider.NAME)
@RequiredArgsConstructor
public class WechatOpenOAuthProvider implements OAuthProvider {
    public static final String NAME = "wechat_open" + OAuthProvider.NAME;

    private final OAuthProperties oAuthProperties;
    private final HttpClientComponent httpClientComponent;

    @Override
    public String buildAuthorizeUrl(String state, String redirectUri) {
        OAuthProperties.OAuthProviderConfig config = oAuthProperties.getProviders().get("wechat_open");
        if (config == null) {
            throw new IllegalArgumentException("微信开放平台 OAuth 配置未找到");
        }
        try {
            String encodedRedirect = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name());
            // 微信开放平台使用 #wechat_redirect 结尾的特殊格式
            return OAuthProviderType.WECHAT_OPEN.getAuthorizeUrl()
                    + "?appid=" + config.getClientId()
                    + "&redirect_uri=" + encodedRedirect
                    + "&response_type=code"
                    + "&scope=snsapi_login"
                    + "&state=" + state
                    + "#wechat_redirect";
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URL 编码失败", e);
        }
    }

    @Override
    public OAuthTokenResponse getAccessToken(String code, String state, String redirectUri) {
        OAuthProperties.OAuthProviderConfig config = oAuthProperties.getProviders().get("wechat_open");
        if (config == null) {
            throw new IllegalArgumentException("微信开放平台 OAuth 配置未找到");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("appid", config.getClientId());
        params.put("secret", config.getClientSecret());
        params.put("code", code);
        params.put("grant_type", "authorization_code");

        HttpClientDto httpClientDto = HttpBuilder.create()
                .setHttpRequest(RequestMethod.GET, "https://api.weixin.qq.com/sns/oauth2/access_token", params)
                .build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);

        JSONObject json = JSONUtil.parseObj(responseBody);
        if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
            throw new RuntimeException("微信 OAuth Token 获取失败: " + json.getStr("errmsg"));
        }
        OAuthTokenResponse resp = new OAuthTokenResponse();
        resp.setAccessToken(json.getStr("access_token"));
        resp.setExpiresIn(json.getLong("expires_in"));
        resp.setRefreshToken(json.getStr("refresh_token"));
        resp.setOpenId(json.getStr("openid"));
        resp.setUnionId(json.getStr("unionid"));
        resp.setScope(json.getStr("scope"));
        return resp;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        OAuthTokenResponse tokenResp = null; // Token info is stored previously; we need openId too
        // The caller should pass both accessToken and openId.
        // We use a simple approach: assume the accessToken includes enough info.
        // Actually, WeChat requires openId for userinfo, so the caller passes it differently.
        // For simplicity, call getAccessToken first and store openId, then call getUserInfo.
        // The OAuthLoginServiceImpl orchestrates this correctly.

        throw new UnsupportedOperationException("微信用户信息获取需要 openId，请在 Service 层协调调用");
    }

    /**
     * 微信获取用户信息需要 openId 和 accessToken
     */
    public OAuthUserInfo getUserInfo(String accessToken, String openId) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", accessToken);
        params.put("openid", openId);
        params.put("lang", "zh_CN");

        HttpClientDto httpClientDto = HttpBuilder.create()
                .setHttpRequest(RequestMethod.GET, "https://api.weixin.qq.com/sns/userinfo", params)
                .build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);

        JSONObject json = JSONUtil.parseObj(responseBody);
        if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
            throw new RuntimeException("微信用户信息获取失败: " + json.getStr("errmsg"));
        }
        OAuthUserInfo info = new OAuthUserInfo();
        info.setOpenId(json.getStr("openid"));
        info.setUnionId(json.getStr("unionid"));
        info.setNickName(json.getStr("nickname"));
        info.setAvatar(json.getStr("headimgurl"));
        info.setSource("WECHAT");
        return info;
    }

    @Override
    public OAuthProviderType getProviderType() {
        return OAuthProviderType.WECHAT_OPEN;
    }
}
