package com.freesia.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 第三方平台授权表 数据传输对象
 * @date 2026-03-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "第三方平台授权表 数据传输对象")
public class SysThirdpartyAuthDto extends BaseDto {
    @Schema(description = "平台+平台唯一id")
    private String authId;
    @Schema(description = "用户来源")
    private String source;
    @Schema(description = "平台编号唯一id")
    private String openId;
    @Schema(description = "登录账号")
    private String userName;
    @Schema(description = "用户昵称")
    private String nickName;
    @Schema(description = "用户邮箱")
    private String email;
    @Schema(description = "头像地址")
    private String avatar;
    @Schema(description = "用户的授权令牌")
    private String accessToken;
    @Schema(description = "用户的授权令牌的有效期，部分平台可能没有")
    private Long expireTimeout;
    @Schema(description = "刷新令牌，部分平台可能没有")
    private String refreshToken;
    @Schema(description = "平台的授权信息，部分平台可能没有")
    private String accessCode;
    @Schema(description = "用户的 unionid")
    private String unionId;
    @Schema(description = "授予的权限，部分平台可能没有")
    private String scope;
    @Schema(description = "个别平台的授权信息，部分平台可能没有")
    private String tokenType;
    @Schema(description = "id token，部分平台可能没有")
    private String idToken;
    @Schema(description = "小米平台用户的附带属性，部分平台可能没有")
    private String macAlgorithm;
    @Schema(description = "小米平台用户的附带属性，部分平台可能没有")
    private String macKey;
    @Schema(description = "用户的授权code，部分平台可能没有")
    private String code;
    @Schema(description = "Twitter平台用户的附带属性，部分平台可能没有")
    private String oauthToken;
    @Schema(description = "Twitter平台用户的附带属性，部分平台可能没有")
    private String oauthTokenSecret;
}
