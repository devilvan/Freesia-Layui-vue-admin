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

/**
 * @author Evad.Wu
 * @Description OSS配置信息表 映射
 * @date 2024-02-28
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_OSS_CONFIG")

@Entity
@Table(name = "SYS_OSS_CONFIG")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "OSS配置信息表 映射")
public class SysOssConfigPo extends BasePo implements Serializable {
    @Schema(description = "配置key")
    @TableField(value = "CONFIG_KEY")
    @Column(name = "CONFIG_KEY", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '配置key'")
    private String configKey;
    @Schema(description = "公钥")
    @TableField(value = "ACCESS_KEY")
    @Column(name = "ACCESS_KEY", columnDefinition = "VARCHAR(255) COMMENT '公钥'")
    private String accessKey;
    @Schema(description = "秘钥")
    @TableField(value = "SECRET_KEY")
    @Column(name = "SECRET_KEY", columnDefinition = "VARCHAR(255) COMMENT '秘钥'")
    private String secretKey;
    @Schema(description = "桶名称")
    @TableField(value = "BUCKET_NAME")
    @Column(name = "BUCKET_NAME", columnDefinition = "VARCHAR(255) COMMENT '桶名称'")
    private String bucketName;
    @Schema(description = "前缀")
    @TableField(value = "FILE_PREFIX")
    @Column(name = "FILE_PREFIX", columnDefinition = "VARCHAR(255) COMMENT '前缀'")
    private String filePrefix;
    @Schema(description = "访问站点")
    @TableField(value = "ENDPOINT")
    @Column(name = "ENDPOINT", columnDefinition = "VARCHAR(255) COMMENT '访问站点'")
    private String endpoint;
    @Schema(description = "自定义域名")
    @TableField(value = "DOMAIN")
    @Column(name = "DOMAIN", columnDefinition = "VARCHAR(255) COMMENT '自定义域名'")
    private String domain;
    @Schema(description = "是否https（0-否 1-是）")
    @TableField(value = "IS_HTTPS")
    @Column(name = "IS_HTTPS", columnDefinition = "BIT(1) COMMENT '是否https（0-否 1-是）'")
    private Boolean isHttps;
    @Schema(description = "域")
    @TableField(value = "REGION")
    @Column(name = "REGION", columnDefinition = "VARCHAR(255) COMMENT '域'")
    private String region;
    @Schema(description = "桶权限类型(PRIVATE PUBLIC CUSTOM)")
    @TableField(value = "ACCESS_POLICY")
    @Column(name = "ACCESS_POLICY", columnDefinition = "VARCHAR(16) NOT NULL COMMENT '桶权限类型(PRIVATE PUBLIC CUSTOM)'")
    private String accessPolicy;
    @Schema(description = "是否默认（0-否 1-是）")
    @TableField(value = "STATUS")
    @Column(name = "STATUS", columnDefinition = "BIT(1) COMMENT '是否默认（0-否 1-是）'")
    private Boolean status;
    @Schema(description = "扩展字段")
    @TableField(value = "EXT1")
    @Column(name = "EXT1", columnDefinition = "VARCHAR(255) COMMENT '扩展字段'")
    private String ext1;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(500) COMMENT '备注'")
    private String remark;
}
