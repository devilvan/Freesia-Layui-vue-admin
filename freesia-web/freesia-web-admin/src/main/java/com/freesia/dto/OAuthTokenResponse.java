package com.freesia.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Bliss.Wu
 * @Description OAuth Token 响应 DTO（各平台 access token 返回统一模型）
 * @date 2026-05-30
 */
@Data
@Schema(description = "OAuth Token 响应")
public class OAuthTokenResponse {
    @Schema(description = "访问令牌")
    @JsonAlias("access_token")
    private String accessToken;

    @Schema(description = "过期时间（秒）")
    @JsonAlias("expires_in")
    private Long expiresIn;

    @Schema(description = "刷新令牌")
    @JsonAlias("refresh_token")
    private String refreshToken;

    @Schema(description = "令牌类型")
    @JsonAlias("token_type")
    private String tokenType;

    @Schema(description = "授权范围")
    @JsonAlias("scope")
    private String scope;

    @Schema(description = "用户唯一标识（平台内）")
    @JsonAlias("user_id")
    private String openId;

    @Schema(description = "联合ID（跨应用）")
    @JsonAlias("union_id")
    private String unionId;

    @Schema(description = "错误码")
    @JsonAlias("error")
    private String error;

    @Schema(description = "错误描述")
    @JsonAlias("error_description")
    private String errorDescription;
}
