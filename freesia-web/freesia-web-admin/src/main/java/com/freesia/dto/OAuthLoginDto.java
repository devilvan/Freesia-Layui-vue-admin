package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Bliss.Wu
 * @Description OAuth 登录请求 DTO
 * @date 2026-05-30
 */
@Data
@Schema(description = "OAuth 登录请求")
public class OAuthLoginDto {
    @Schema(description = "平台编码(gitee/github/wechat_open/wechat_mini)")
    private String provider;

    @Schema(description = "授权码")
    private String code;

    @Schema(description = "防CSRF状态码")
    private String state;

    @Schema(description = "客户端key（SYS_CLIENT.clientKey）")
    private String clientKey;

    @Schema(description = "客户端密钥（SYS_CLIENT.clientSecret）")
    private String clientSecret;

    @Schema(description = "登录成功后前端跳转地址")
    private String redirectUrl;
}
