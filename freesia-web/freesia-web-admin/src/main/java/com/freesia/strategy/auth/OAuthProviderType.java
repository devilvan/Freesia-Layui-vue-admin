package com.freesia.strategy.auth;

import com.freesia.util.UEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Bliss.Wu
 * @Description OAuth 第三方平台类型枚举
 * @date 2026-05-30
 */
@Getter
@AllArgsConstructor
public enum OAuthProviderType {
    GITEE("gitee", "Gitee", "https://gitee.com/oauth/authorize"),
    GITHUB("github", "GitHub", "https://github.com/login/oauth/authorize"),
    WECHAT_OPEN("wechat_open", "WeChatOpenPlatform", "https://open.weixin.qq.com/connect/qrconnect"),
    WECHAT_MINI("wechat_mini", "WeChatMiniProgram", null),
    ;

    private final String code;
    private final String desc;
    private final String authorizeUrl;

    public static OAuthProviderType getInstanceByCode(String code) {
        if (UEmpty.isEmpty(code)) {
            return null;
        }
        for (OAuthProviderType value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
