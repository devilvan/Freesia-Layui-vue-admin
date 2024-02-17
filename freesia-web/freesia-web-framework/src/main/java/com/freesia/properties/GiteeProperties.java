package com.freesia.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Evad.Wu
 * @Description Gitee模块相关 配置信息属性
 * @date 2024-01-23
 */
@Data
@Component
@ConfigurationProperties(prefix = "gitee")
public class GiteeProperties {
    @Schema(description = "请求所有提交记录功能配置")
    private Commits commits;
    @Schema(description = "请求Oauth获取认证令牌")
    private Oauth oauth;

    /**
     * 请求所有提交记录功能配置
     */
    @Data
    public static class Commits {
        @Schema(description = "URL")
        private String url;
    }

    /**
     * 请求Oauth获取认证令牌
     */
    @Data
    public static class Oauth {
        @Schema(description = "URL")
        private String url;
        @Schema(description = "每页请求的数量")
        private Integer perPage;
    }
}
