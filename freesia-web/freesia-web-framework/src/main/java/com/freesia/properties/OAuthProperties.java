package com.freesia.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bliss.Wu
 * @Description OAuth 第三方登录配置属性
 * @date 2026-05-30
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {
    @Schema(description = "各平台 OAuth 配置，key 为平台编码(gitee/github/wechat_open)")
    private Map<String, OAuthProviderConfig> providers = new HashMap<>();

    @Data
    public static class OAuthProviderConfig {
        @Schema(description = "客户端ID（AppId）")
        private String clientId;

        @Schema(description = "客户端密钥（AppSecret）")
        private String clientSecret;

        @Schema(description = "OAuth 回调地址（指向后端回调接口）")
        private String redirectUri;

        @Schema(description = "授权范围")
        private List<String> scopes;
    }
}
