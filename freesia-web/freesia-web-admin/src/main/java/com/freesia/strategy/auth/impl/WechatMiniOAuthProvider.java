package com.freesia.strategy.auth.impl;

import com.freesia.dto.OAuthTokenResponse;
import com.freesia.dto.OAuthUserInfo;
import com.freesia.strategy.auth.OAuthProvider;
import com.freesia.strategy.auth.OAuthProviderType;
import org.springframework.stereotype.Component;

/**
 * @author Bliss.Wu
 * @Description 微信小程序 Provider（委托给现有的 wxLogin 流程，不走 OAuth 重定向）
 * @date 2026-05-30
 */
@Component(WechatMiniOAuthProvider.NAME)
public class WechatMiniOAuthProvider implements OAuthProvider {
    public static final String NAME = "wechat_mini" + OAuthProvider.NAME;

    @Override
    public String buildAuthorizeUrl(String state, String redirectUri) {
        // 小程序不走重定向流程，由前端调用 wx.login 获取 code 后直接 POST 到后端
        return null;
    }

    @Override
    public OAuthTokenResponse getAccessToken(String code, String state, String redirectUri) {
        // 小程序 code 换 session 的逻辑已由 SysLoginServiceImpl.wxLogin() 处理
        return null;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        return null;
    }

    @Override
    public OAuthProviderType getProviderType() {
        return OAuthProviderType.WECHAT_MINI;
    }
}
