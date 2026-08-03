package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freesia.po.BasePo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 第三方平台授权表 映射
 * @date 2026-03-13
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_THIRDPARTY_AUTH")

@Entity
@Table(name = "SYS_THIRDPARTY_AUTH")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "第三方平台授权表 映射")
public class SysThirdpartyAuthPo extends BasePo implements Serializable {
    @Schema(description = "平台+平台唯一id")
    @TableField(value = "AUTH_ID")
    @Column(name = "AUTH_ID", columnDefinition = "VARCHAR(255) NOT NULL COMMENT '平台+平台唯一id'")
    private String authId;
    @Schema(description = "用户来源")
    @TableField(value = "SOURCE")
    @Column(name = "SOURCE", columnDefinition = "VARCHAR(255) NOT NULL COMMENT '用户来源'")
    private String source;
    @Schema(description = "平台编号唯一id")
    @TableField(value = "OPEN_ID")
    @Column(name = "OPEN_ID", columnDefinition = "VARCHAR(255) COMMENT '平台编号唯一id'")
    private String openId;
    @Schema(description = "登录账号")
    @TableField(value = "USER_NAME")
    @Column(name = "USER_NAME", columnDefinition = "VARCHAR(30) NOT NULL COMMENT '登录账号'")
    private String userName;
    @Schema(description = "用户昵称")
    @TableField(value = "NICK_NAME")
    @Column(name = "NICK_NAME", columnDefinition = "VARCHAR(30) COMMENT '用户昵称'")
    private String nickName;
    @Schema(description = "用户邮箱")
    @TableField(value = "EMAIL")
    @Column(name = "EMAIL", columnDefinition = "VARCHAR(255) COMMENT '用户邮箱'")
    private String email;
    @Schema(description = "头像地址")
    @TableField(value = "AVATAR")
    @Column(name = "AVATAR", columnDefinition = "VARCHAR(500) COMMENT '头像地址'")
    private String avatar;
    @Schema(description = "用户的授权令牌")
    @TableField(value = "ACCESS_TOKEN")
    @Column(name = "ACCESS_TOKEN", columnDefinition = "VARCHAR(2,000) NOT NULL COMMENT '用户的授权令牌'")
    private String accessToken;
    @Schema(description = "用户的授权令牌的有效期，部分平台可能没有")
    @TableField(value = "EXPIRE_TIMEOUT")
    @Column(name = "EXPIRE_TIMEOUT", columnDefinition = "BIGINT(19) COMMENT '用户的授权令牌的有效期，部分平台可能没有'")
    private Long expireTimeout;
    @Schema(description = "刷新令牌，部分平台可能没有")
    @TableField(value = "REFRESH_TOKEN")
    @Column(name = "REFRESH_TOKEN", columnDefinition = "VARCHAR(255) COMMENT '刷新令牌，部分平台可能没有'")
    private String refreshToken;
    @Schema(description = "平台的授权信息，部分平台可能没有")
    @TableField(value = "ACCESS_CODE")
    @Column(name = "ACCESS_CODE", columnDefinition = "VARCHAR(2,000) COMMENT '平台的授权信息，部分平台可能没有'")
    private String accessCode;
    @Schema(description = "用户的 unionid")
    @TableField(value = "UNION_ID")
    @Column(name = "UNION_ID", columnDefinition = "VARCHAR(255) COMMENT '用户的 unionid'")
    private String unionId;
    @Schema(description = "授予的权限，部分平台可能没有")
    @TableField(value = "SCOPE")
    @Column(name = "SCOPE", columnDefinition = "VARCHAR(255) COMMENT '授予的权限，部分平台可能没有'")
    private String scope;
    @Schema(description = "个别平台的授权信息，部分平台可能没有")
    @TableField(value = "TOKEN_TYPE")
    @Column(name = "TOKEN_TYPE", columnDefinition = "VARCHAR(255) COMMENT '个别平台的授权信息，部分平台可能没有'")
    private String tokenType;
    @Schema(description = "id token，部分平台可能没有")
    @TableField(value = "ID_TOKEN")
    @Column(name = "ID_TOKEN", columnDefinition = "VARCHAR(2,000) COMMENT 'id token，部分平台可能没有'")
    private String idToken;
    @Schema(description = "小米平台用户的附带属性，部分平台可能没有")
    @TableField(value = "MAC_ALGORITHM")
    @Column(name = "MAC_ALGORITHM", columnDefinition = "VARCHAR(255) COMMENT '小米平台用户的附带属性，部分平台可能没有'")
    private String macAlgorithm;
    @Schema(description = "小米平台用户的附带属性，部分平台可能没有")
    @TableField(value = "MAC_KEY")
    @Column(name = "MAC_KEY", columnDefinition = "VARCHAR(255) COMMENT '小米平台用户的附带属性，部分平台可能没有'")
    private String macKey;
    @Schema(description = "用户的授权code，部分平台可能没有")
    @TableField(value = "CODE")
    @Column(name = "CODE", columnDefinition = "VARCHAR(255) COMMENT '用户的授权code，部分平台可能没有'")
    private String code;
    @Schema(description = "Twitter平台用户的附带属性，部分平台可能没有")
    @TableField(value = "OAUTH_TOKEN")
    @Column(name = "OAUTH_TOKEN", columnDefinition = "VARCHAR(255) COMMENT 'Twitter平台用户的附带属性，部分平台可能没有'")
    private String oauthToken;
    @Schema(description = "Twitter平台用户的附带属性，部分平台可能没有")
    @TableField(value = "OAUTH_TOKEN_SECRET")
    @Column(name = "OAUTH_TOKEN_SECRET", columnDefinition = "VARCHAR(255) COMMENT 'Twitter平台用户的附带属性，部分平台可能没有'")
    private String oauthTokenSecret;
}
