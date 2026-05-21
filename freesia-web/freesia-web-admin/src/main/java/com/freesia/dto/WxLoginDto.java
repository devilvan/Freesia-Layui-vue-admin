package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Evad.Wu
 * @Description 微信登录功能 数据传输对象
 * @date 2026-05-20
 */
@Data
@Schema(description = "微信登录功能 数据传输对象")
public class WxLoginDto {
    @Schema(description = "微信授权code")
    private String code;

    @Schema(description = "微信用户唯一标识openId")
    private String openId;

    @Schema(description = "微信用户UnionID")
    private String unionId;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "客户端key")
    private String clientKey;

    @Schema(description = "客户端密钥")
    private String clientSecret;

    @Schema(description = "重定向URL(登录成功后跳转地址)")
    private String redirectUrl;
}