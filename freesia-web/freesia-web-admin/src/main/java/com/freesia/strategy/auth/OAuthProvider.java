package com.freesia.strategy.auth;

import com.freesia.dto.OAuthTokenResponse;
import com.freesia.dto.OAuthUserInfo;

/**
 * @author Bliss.Wu
 * @Description OAuth 第三方平台 Provider 接口
 * @date 2026-05-30
 */
public interface OAuthProvider {
    String NAME = "OAuthProvider";

    /**
     * 构建第三方授权页 URL
     *
     * @param state       防 CSRF 的状态码
     * @param redirectUri 回调地址
     * @return 完整的授权页 URL
     */
    String buildAuthorizeUrl(String state, String redirectUri);

    /**
     * 用授权码换取 access token
     *
     * @param code        授权码
     * @param state       状态码
     * @param redirectUri 回调地址
     * @return Token 响应
     */
    OAuthTokenResponse getAccessToken(String code, String state, String redirectUri);

    /**
     * 获取第三方平台用户信息
     *
     * @param accessToken 访问令牌
     * @return 用户信息
     */
    OAuthUserInfo getUserInfo(String accessToken);

    /**
     * 返回当前 Provider 对应的平台类型
     */
    OAuthProviderType getProviderType();
}
