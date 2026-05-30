package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Bliss.Wu
 * @Description OAuth 用户信息 DTO（各平台用户信息统一模型）
 * @date 2026-05-30
 */
@Data
@Schema(description = "OAuth 用户信息")
public class OAuthUserInfo {
    @Schema(description = "用户唯一标识")
    private String openId;

    @Schema(description = "联合ID")
    private String unionId;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "平台来源")
    private String source;
}
