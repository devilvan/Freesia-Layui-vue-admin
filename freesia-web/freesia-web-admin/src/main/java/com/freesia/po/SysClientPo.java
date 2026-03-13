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

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 系统用户授权表 映射
 * @date 2026-03-13
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_CLIENT")

@Entity
@Table(name = "SYS_CLIENT")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "系统用户授权表 映射")
public class SysClientPo extends BasePo implements Serializable {
    @Schema(description = "客户端ID")
    @TableField(value = "CLIENT_ID")
    @Column(name = "CLIENT_ID", columnDefinition = "BIGINT(19) COMMENT '客户端ID'")
    private Long clientId;
    @Schema(description = "客户端key")
    @TableField(value = "CLIENT_KEY")
    @Column(name = "CLIENT_KEY", columnDefinition = "VARCHAR(64) COMMENT '客户端key'")
    private String clientKey;
    @Schema(description = "客户端秘钥")
    @TableField(value = "CLIENT_SECRET")
    @Column(name = "CLIENT_SECRET", columnDefinition = "VARCHAR(255) COMMENT '客户端秘钥'")
    private String clientSecret;
    @Schema(description = "授权类型")
    @TableField(value = "GRANT_TYPE")
    @Column(name = "GRANT_TYPE", columnDefinition = "VARCHAR(32) COMMENT '授权类型'")
    private String grantType;
    @Schema(description = "设备类型")
    @TableField(value = "DEVICE_TYPE")
    @Column(name = "DEVICE_TYPE", columnDefinition = "VARCHAR(32) COMMENT '设备类型'")
    private String deviceType;
    @Schema(description = "token活跃超时时间（单位：秒）")
    @TableField(value = "ACTIVE_TIMEOUT")
    @Column(name = "ACTIVE_TIMEOUT", columnDefinition = "INT(10) COMMENT 'token活跃超时时间（单位：秒）'")
    private Integer activeTimeout;
    @Schema(description = "token固定超时（单位：秒）")
    @TableField(value = "TIMEOUT")
    @Column(name = "TIMEOUT", columnDefinition = "INT(10) COMMENT 'token固定超时（单位：秒）'")
    private Integer timeout;
}
