package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 系统列中间表 映射
 * @date 2026-03-27
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_COLUMN_MIDDLE")

@Entity
@Table(name = "SYS_COLUMN_MIDDLE")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "系统列中间表 映射")
public class SysColumnMiddlePo extends BasePo implements Serializable {
    @Schema(description = "系统列头ID")
    @TableField(value = "HEADER_ID")
    @Column(name = "HEADER_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '系统列头ID'")
    private Long headerId;
    @Schema(description = "列名")
    @TableField(value = "TITLE")
    @Column(name = "TITLE", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '列名'")
    private String title;
    @Schema(description = "属性名")
    @TableField(value = "NAME")
    @Column(name = "NAME", columnDefinition = "VARCHAR(64) COMMENT '属性名'")
    private String name;
    @Schema(description = "是否可用（true-是；false-否）")
    @TableField(value = "ENABLED")
    @Column(name = "ENABLED", columnDefinition = "BIT(1) COMMENT '是否可用（true-是；false-否）'")
    private Boolean enabled;
    @Schema(description = "自定义插槽")
    @TableField(value = "CUSTOM_SLOT")
    @Column(name = "CUSTOM_SLOT", columnDefinition = "VARCHAR(64) COMMENT '自定义插槽'")
    private String customSlot;
}
