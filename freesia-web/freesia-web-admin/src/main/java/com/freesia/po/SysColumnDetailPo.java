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
 * @Description 系统列明细表 映射
 * @date 2026-03-17
 */
@Setter
@Getter
@ToString
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
    @Schema(description = "列名")
    @TableField(value = "TITLE")
    @Column(name = "TITLE", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '列名'")
    private String title;
    @Schema(description = "是否启用（true-是；false-否）")
    @TableField(value = "ENABLED")
    @Column(name = "ENABLED", columnDefinition = "BIT(1) COMMENT '是否启用（true-是；false-否）'")
    private Boolean enabled;
    @Schema(description = "是否固定（true-是；false-否）")
    @TableField(value = "FIXED")
    @Column(name = "FIXED", columnDefinition = "BIT(1) COMMENT '是否固定（true-是；false-否）'")
    private Boolean fixed;
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
    @Schema(description = "最大列宽（单位：px）")
    @TableField(value = "MAX_WIDTH")
    @Column(name = "MAX_WIDTH", columnDefinition = "INT(10) COMMENT '最大列宽（单位：px）'")
    private Integer maxWidth;
    @Schema(description = "排序号")
    @TableField(value = "ORDER_NUM")
    @Column(name = "ORDER_NUM", columnDefinition = "INT(10) COMMENT '排序号'")
    private Integer orderNum;
    @Schema(description = "是否排序（null-不排序；A-顺序；D-倒序）")
    @TableField(value = "SORTED")
    @Column(name = "SORTED", columnDefinition = "CHAR(1) COMMENT '是否排序（null-不排序；A-顺序；D-倒序）'")
    private String sorted;
}
