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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description URL配置信息表 映射
 * @date 2024-01-24
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "URL_CONFIG")

@Entity
@Table(name = "URL_CONFIG")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "URL配置信息表 映射")
public class UrlConfigPo extends BasePo implements Serializable {
    @Serial
    private static final long serialVersionUID = -3913462131718220503L;
    @Schema(description = "配置标识")
    @TableField(value = "CODE")
    @Column(name = "CODE", columnDefinition = "VARCHAR(64) COMMENT '配置标识'", unique = true)
    private String code;
    @Schema(description = "网址")
    @TableField(value = "URL")
    @Column(name = "URL", columnDefinition = "TEXT(65535) COMMENT '网址'")
    private String url;
    @Schema(description = "请求方式")
    @TableField(value = "REQUEST_TYPE")
    @Column(name = "REQUEST_TYPE", columnDefinition = "VARCHAR(16) COMMENT '请求方式'")
    private String requestType;
    @Schema(description = "请求头信息")
    @TableField(value = "HEADER")
    @Column(name = "HEADER", columnDefinition = "TEXT(65535) COMMENT '请求头信息'")
    private String header;
    @Schema(description = "请求参数")
    @TableField(value = "PARAM")
    @Column(name = "PARAM", columnDefinition = "TEXT(65535) COMMENT '请求参数'")
    private String param;
    @Schema(description = "内容类型")
    @TableField(value = "CONTENT_TYPE")
    @Column(name = "CONTENT_TYPE", columnDefinition = "VARCHAR(128) COMMENT '内容类型'")
    private String contentType;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(128) COMMENT '备注'")
    private String remark;

}
