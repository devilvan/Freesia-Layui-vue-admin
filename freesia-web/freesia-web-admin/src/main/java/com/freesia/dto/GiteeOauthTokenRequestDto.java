package com.freesia.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.util.UString;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Arrays;

/**
 * @author Evad.Wu
 * @Description 请求gitee获取access_token接口-入参 数据传输类
 * @date 2024-01-22
 */
@Data
@Schema(description = "请求gitee获取access_token接口-入参 数据传输类")
public class GiteeOauthTokenRequestDto {
    @Schema(description = "授权类型")
    @JSONField(name = "grant_type")
    private String grantType;
    @Schema(description = "用户名")
    @JSONField(name = "username")
    private String userName;
    @Schema(description = "password")
    private String password;
    @Schema(description = "用户ID")
    @JSONField(name = "client_id")
    private String clientId;
    @Schema(description = "用户秘钥")
    @JSONField(name = "client_secret")
    private String clientSecret;
    /**
     * 注意：如果gitee个人设置-第三方应用中只开启部分权限，在设置未开启的权限时会返回error信息
     */
    @Schema(description = "授权范围")
    private String scope;

    public void setScope(Scope... scopeArr) {
        String[] scopeCodeArr = Arrays.stream(scopeArr).map(Scope::getCode).toArray(String[]::new);
        this.scope = UString.join(scopeCodeArr, " ");
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
