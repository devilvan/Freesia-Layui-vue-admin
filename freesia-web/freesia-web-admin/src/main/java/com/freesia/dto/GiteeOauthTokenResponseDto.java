package com.freesia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freesia.util.UCollection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 请求gitee获取access_token接口-入参 数据传输类
 * @date 2024-01-22
 */
@Data
@Schema(description = "请求gitee获取access_token接口-入参 数据传输类")
public class GiteeOauthTokenResponseDto {
    public static final String URL = "https://gitee.com/oauth/token";

    @Schema(description = "授权类型")
    @JsonProperty(value = "grant_type")
    private String grantType;
    @Schema(description = "用户名")
    @JsonProperty(value = "username")
    private String userName;
    @Schema(description = "password")
    private String password;
    @Schema(description = "用户ID")
    @JsonProperty(value = "client_id")
    private String clientId;
    @Schema(description = "用户秘钥")
    @JsonProperty(value = "client_secret")
    private String clientSecret;
    @Schema(description = "授权范围")
    private String scope;

    public void setScope(Scope... scopeArr) {
        Set<String> scopeCodeSet = Arrays.stream(scopeArr).map(Scope::getCode).collect(Collectors.toSet());
        this.scope = UCollection.join(scopeCodeSet, ",");
    }

    /**
     * @author Evad.Wu
     * @Description 授权范围 枚举
     * @date 2024-01-23
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Scope {
        /**
         * 访问你的个人信息、最新动态等
         */
        USER_INFO("user_info"),
        /**
         * 查看、创建、更新你的项目
         */
        PROJECTS("projects"),
        /**
         * 查看、发布、更新你的 Pull Request
         */
        PULL_REQUESTS("pull_requests"),
        /**
         * 查看、发布、更新你的 Issue
         */
        ISSUES("issues"),
        /**
         * 查看、发布、管理你在项目、代码片段中的评论
         */
        NOTES("notes"),
        /**
         * 查看、部署、删除你的公钥
         */
        KEYS("keys"),
        /**
         * 查看、部署、更新你的 Webhook
         */
        HOOK("hook"),
        /**
         * 查看、创建、更新你的项目
         */
        GROUPS("groups"),
        /**
         * 查看、创建、更新你的项目
         */
        GISTS("gists"),
        /**
         * 查看、创建、更新你的项目
         */
        ENTERPRISES("enterprises");

        @Schema(description = "授权范围编码")
        private String code;
    }
}
