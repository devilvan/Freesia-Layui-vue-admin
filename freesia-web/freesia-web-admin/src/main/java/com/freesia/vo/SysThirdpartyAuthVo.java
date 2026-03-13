package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 第三方平台授权表 值对象
 * @date 2026-03-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "第三方平台授权表 值对象")
public class SysThirdpartyAuthVo extends BaseVo {
    @Schema(description = "平台+平台唯一id")
    @JsonAlias(value = {"authId"})
    private String authId;
    @Schema(description = "用户来源")
    @JsonAlias(value = {"source"})
    private String source;
    @Schema(description = "平台编号唯一id")
    @JsonAlias(value = {"openId"})
    private String openId;
    @Schema(description = "登录账号")
    @JsonAlias(value = {"userName"})
    private String userName;
    @Schema(description = "用户昵称")
    @JsonAlias(value = {"nickName"})
    private String nickName;
    @Schema(description = "用户邮箱")
    @JsonAlias(value = {"email"})
    private String email;
    @Schema(description = "头像地址")
    @JsonAlias(value = {"avatar"})
    private String avatar;
    @Schema(description = "用户的授权令牌")
    @JsonAlias(value = {"accessToken"})
    private String accessToken;
    @Schema(description = "用户的授权令牌的有效期，部分平台可能没有")
    @JsonAlias(value = {"expireTimeout"})
    private Long expireTimeout;
    @Schema(description = "刷新令牌，部分平台可能没有")
    @JsonAlias(value = {"refreshToken"})
    private String refreshToken;
    @Schema(description = "平台的授权信息，部分平台可能没有")
    @JsonAlias(value = {"accessCode"})
    private String accessCode;
    @Schema(description = "用户的 unionid")
    @JsonAlias(value = {"unionId"})
    private String unionId;
    @Schema(description = "授予的权限，部分平台可能没有")
    @JsonAlias(value = {"scope"})
    private String scope;
    @Schema(description = "个别平台的授权信息，部分平台可能没有")
    @JsonAlias(value = {"tokenType"})
    private String tokenType;
    @Schema(description = "id token，部分平台可能没有")
    @JsonAlias(value = {"idToken"})
    private String idToken;
    @Schema(description = "小米平台用户的附带属性，部分平台可能没有")
    @JsonAlias(value = {"macAlgorithm"})
    private String macAlgorithm;
    @Schema(description = "小米平台用户的附带属性，部分平台可能没有")
    @JsonAlias(value = {"macKey"})
    private String macKey;
    @Schema(description = "用户的授权code，部分平台可能没有")
    @JsonAlias(value = {"code"})
    private String code;
    @Schema(description = "Twitter平台用户的附带属性，部分平台可能没有")
    @JsonAlias(value = {"oauthToken"})
    private String oauthToken;
    @Schema(description = "Twitter平台用户的附带属性，部分平台可能没有")
    @JsonAlias(value = {"oauthTokenSecret"})
    private String oauthTokenSecret;
}
