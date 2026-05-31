package com.freesia.strategy.auth.impl;

import cn.hutool.json.JSONArray;
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
 * @Description GitHub OAuth 2.0 Provider
 * @date 2026-05-30
 */
@Component(GithubOAuthProvider.NAME)
@RequiredArgsConstructor
public class GithubOAuthProvider implements OAuthProvider {
    public static final String NAME = "github" + OAuthProvider.NAME;

    private final OAuthProperties oAuthProperties;
    private final HttpClientComponent httpClientComponent;

    @Override
    public String buildAuthorizeUrl(String state, String redirectUri) {
        OAuthProperties.OAuthProviderConfig config = oAuthProperties.getProviders().get("github");
        if (config == null) {
            throw new IllegalArgumentException("GitHub OAuth 配置未找到");
        }
        StringBuilder url = new StringBuilder(OAuthProviderType.GITHUB.getAuthorizeUrl());
        url.append("?client_id=").append(config.getClientId());
        try {
            url.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            url.append("&redirect_uri=").append(redirectUri);
        }
        url.append("&state=").append(state);
        if (config.getScopes() != null && !config.getScopes().isEmpty()) {
            url.append("&scope=").append(String.join(" ", config.getScopes()));
        }
        return url.toString();
    }

    @Override
    public OAuthTokenResponse getAccessToken(String code, String state, String redirectUri) {
        OAuthProperties.OAuthProviderConfig config = oAuthProperties.getProviders().get("github");
        if (config == null) {
            throw new IllegalArgumentException("GitHub OAuth 配置未找到");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", config.getClientId());
        params.put("client_secret", config.getClientSecret());
        params.put("code", code);
        params.put("redirect_uri", redirectUri);
        params.put("state", state);

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        HttpClientDto httpClientDto = HttpBuilder.create()
                .setHttpRequest(RequestMethod.POST, "https://github.com/login/oauth/access_token", params)
                .setHeaders(headers)
                .build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);

        JSONObject json = JSONUtil.parseObj(responseBody);
        if (json.containsKey("error")) {
            throw new RuntimeException("GitHub OAuth Token 获取失败: " + json.getStr("error_description"));
        }
        OAuthTokenResponse resp = new OAuthTokenResponse();
        resp.setAccessToken(json.getStr("access_token"));
        resp.setTokenType(json.getStr("token_type"));
        resp.setScope(json.getStr("scope"));
        return resp;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "token " + accessToken);
        headers.put("Accept", "application/json");

        HttpClientDto httpClientDto = HttpBuilder.create()
                .setHttpRequest(RequestMethod.GET, "https://api.github.com/user", null)
                .setHeaders(headers)
                .build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);

        JSONObject json = JSONUtil.parseObj(responseBody);
        OAuthUserInfo info = new OAuthUserInfo();
        info.setOpenId(String.valueOf(json.getInt("id")));
        info.setNickName(json.getStr("login"));
        info.setAvatar(json.getStr("avatar_url"));
        info.setEmail(json.getStr("email"));
        info.setSource("GITHUB");

        // GitHub 可能不直接返回 email，需要单独请求
        if (info.getEmail() == null || info.getEmail().isEmpty()) {
            HttpClientDto emailDto = HttpBuilder.create()
                    .setHttpRequest(RequestMethod.GET, "https://api.github.com/user/emails", null)
                    .setHeaders(headers)
                    .build();
            String emailBody = httpClientComponent.doExecute(emailDto);
            JSONArray emails = JSONUtil.parseArray(emailBody);
            if (emails != null && !emails.isEmpty()) {
                for (int i = 0; i < emails.size(); i++) {
                    JSONObject emailObj = emails.getJSONObject(i);
                    if (emailObj.getBool("primary", false)) {
                        info.setEmail(emailObj.getStr("email"));
                        break;
                    }
                }
                // 如果没有 primary 的，取第一个已验证的
                if (info.getEmail() == null || info.getEmail().isEmpty()) {
                    for (int i = 0; i < emails.size(); i++) {
                        JSONObject emailObj = emails.getJSONObject(i);
                        if (emailObj.getBool("verified", false)) {
                            info.setEmail(emailObj.getStr("email"));
                            break;
                        }
                    }
                }
            }
        }
        return info;
    }

    @Override
    public OAuthProviderType getProviderType() {
        return OAuthProviderType.GITHUB;
    }
}
