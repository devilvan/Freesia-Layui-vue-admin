package com.freesia.dto;

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
    private String accessToken;

    @Schema(description = "过期时间（秒）")
    private Long expiresIn;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "令牌类型")
    private String tokenType;

    @Schema(description = "授权范围")
    private String scope;

    @Schema(description = "用户唯一标识（平台内）")
    private String openId;

    @Schema(description = "联合ID（跨应用）")
    private String unionId;
}
