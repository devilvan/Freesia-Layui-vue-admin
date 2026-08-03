package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 映射
 * @date 2026-03-27
 */
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_COLUMN_DETAIL")

@Entity
@Table(name = "SYS_COLUMN_DETAIL")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "系统列明细表 映射")
public class SysColumnDetailPo extends BasePo implements Serializable {
    @Schema(description = "用户ID")
    @TableField(value = "USER_ID")
    @Column(name = "USER_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '用户ID'")
    private Long userId;
    @Schema(description = "系统列头ID")
    @TableField(value = "HEADER_ID")
    @Column(name = "HEADER_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '系统列头ID'")
    private Long headerId;
    @Schema(description = "系统列中间表ID")
    @TableField(value = "MIDDLE_ID")
    @Column(name = "MIDDLE_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '系统列中间表ID'")
    private Long middleId;
    @Schema(description = "列名")
    @TableField(value = "TITLE")
    @Column(name = "TITLE", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '列名'")
    private String title;
    @Schema(description = "属性名")
    @TableField(value = "NAME")
    @Column(name = "NAME", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '属性名'")
    private String name;
    @Schema(description = "是否启用（true-是；false-否）")
    @TableField(value = "ENABLED")
    @Column(name = "ENABLED", columnDefinition = "BIT(1) COMMENT '是否启用（true-是；false-否）'")
    private Boolean enabled;
    @Schema(description = "固定（null-不固定；left-左固定；right-右固定）")
    @TableField(value = "FIXED")
    @Column(name = "FIXED", columnDefinition = "VARCHAR(8) COMMENT '固定（null-不固定；left-左固定；right-右固定）'")
    private String fixed;
    @Schema(description = "是否过长省略（true-是；false-否）")
    @TableField(value = "ELLIPSIS_TOOLTIP")
    @Column(name = "ELLIPSIS_TOOLTIP", columnDefinition = "BIT(1) COMMENT '是否过长省略（true-是；false-否）'")
    private Boolean ellipsisTooltip;
    @Schema(description = "列宽（单位：px）")
    @TableField(value = "WIDTH")
    @Column(name = "WIDTH", columnDefinition = "INT(10) COMMENT '列宽（单位：px）'")
    private Integer width;
    @Schema(description = "最小列宽（单位：px）")
    @TableField(value = "MIN_WIDTH")
    @Column(name = "MIN_WIDTH", columnDefinition = "INT(10) COMMENT '最小列宽（单位：px）'")
    private Integer minWidth;
    @Schema(description = "排序号")
    @TableField(value = "ORDER_NUM")
    @Column(name = "ORDER_NUM", columnDefinition = "INT(10) COMMENT '排序号'")
    private Integer orderNum;
    @Schema(description = "是否排序（null-不排序；asc-顺序；desc-倒序）")
    @TableField(value = "SORTED")
    @Column(name = "SORTED", columnDefinition = "VARCHAR(4) COMMENT '是否排序（null-不排序；asc-顺序；desc-倒序）'")
    private String sorted;
    @Schema(description = "是否允许调整宽度（true-是；false-否）")
    @TableField(value = "RESIZE_FLAG")
    @Column(name = "RESIZE_FLAG", columnDefinition = "BIT(1) COMMENT '是否允许调整宽度（true-是；false-否）'")
    private Boolean resizeFlag;
}
