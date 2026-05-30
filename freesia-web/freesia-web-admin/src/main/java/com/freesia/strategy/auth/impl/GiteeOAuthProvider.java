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
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bliss.Wu
 * @Description Gitee OAuth 2.0 Provider
 * @date 2026-05-30
 */
@Component(GiteeOAuthProvider.NAME)
@RequiredArgsConstructor
public class GiteeOAuthProvider implements OAuthProvider {
    public static final String NAME = "gitee" + OAuthProvider.NAME;

    private final OAuthProperties oAuthProperties;
    private final HttpClientComponent httpClientComponent;

    @Override
    public String buildAuthorizeUrl(String state, String redirectUri) {
        OAuthProperties.OAuthProviderConfig config = oAuthProperties.getProviders().get("gitee");
        if (config == null) {
            throw new IllegalArgumentException("Gitee OAuth 配置未找到");
        }
        StringBuilder url = new StringBuilder(OAuthProviderType.GITEE.getAuthorizeUrl());
        url.append("?client_id=").append(config.getClientId());
        url.append("&redirect_uri=").append(redirectUri);
        url.append("&response_type=code");
        url.append("&state=").append(state);
        if (config.getScopes() != null && !config.getScopes().isEmpty()) {
            url.append("&scope=").append(String.join(" ", config.getScopes()));
        }
        return url.toString();
    }

    @Override
    public OAuthTokenResponse getAccessToken(String code, String state, String redirectUri) {
        OAuthProperties.OAuthProviderConfig config = oAuthProperties.getProviders().get("gitee");
        if (config == null) {
            throw new IllegalArgumentException("Gitee OAuth 配置未找到");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", config.getClientId());
        params.put("client_secret", config.getClientSecret());
        params.put("redirect_uri", redirectUri); // Gitee 要求此参数

        HttpClientDto httpClientDto = HttpBuilder.create()
                .setHttpRequest(RequestMethod.POST, "https://gitee.com/oauth/token", params)
                .build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);

        JSONObject json = JSONUtil.parseObj(responseBody);
        if (json.containsKey("error")) {
            throw new RuntimeException("Gitee OAuth Token 获取失败: " + json.getStr("error_description"));
        }
        OAuthTokenResponse resp = new OAuthTokenResponse();
        resp.setAccessToken(json.getStr("access_token"));
        resp.setExpiresIn(json.getLong("expires_in"));
        resp.setRefreshToken(json.getStr("refresh_token"));
        resp.setTokenType(json.getStr("token_type"));
        resp.setScope(json.getStr("scope"));
        return resp;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        Map<String, Object> params = new HashMap<>();
        params.put("access_token", accessToken);

        HttpClientDto httpClientDto = HttpBuilder.create()
                .setHttpRequest(RequestMethod.GET, "https://gitee.com/api/v5/user", params)
                .build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);

        JSONObject json = JSONUtil.parseObj(responseBody);
        OAuthUserInfo info = new OAuthUserInfo();
        info.setOpenId(json.getStr("id"));
        info.setNickName(UEmpty.defaultIfEmpty(json.getStr("name"), json.getStr("login")));
        info.setAvatar(json.getStr("avatar_url"));
        info.setEmail(json.getStr("email"));
        info.setSource("GITEE");
        return info;
    }

    @Override
    public OAuthProviderType getProviderType() {
        return OAuthProviderType.GITEE;
    }
}
