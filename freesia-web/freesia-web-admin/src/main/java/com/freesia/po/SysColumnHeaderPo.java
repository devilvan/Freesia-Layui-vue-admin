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
 * @Description 系统列头表 映射
 * @date 2026-03-27
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_COLUMN_HEADER")

@Entity
@Table(name = "SYS_COLUMN_HEADER")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "系统列头表 映射")
public class SysColumnHeaderPo extends BasePo implements Serializable {
    @Schema(description = "组件ID")
    @TableField(value = "COMPONENT_ID")
    @Column(name = "COMPONENT_ID", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '组件ID'")
    private String componentId;
    @Schema(description = "组件名")
    @TableField(value = "NAME")
    @Column(name = "NAME", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '组件名'")
    private String name;
    @Schema(description = "组件描述")
    @TableField(value = "DESCRIPTION")
    @Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(64) COMMENT '组件描述'")
    private String description;
    @Schema(description = "表格高度")
    @TableField(value = "HEIGHT")
    @Column(name = "HEIGHT", columnDefinition = "INT(10) COMMENT '表格高度'")
    private Integer height;
    @Schema(description = "表格最大高度")
    @TableField(value = "MAX_HEIGHT")
    @Column(name = "MAX_HEIGHT", columnDefinition = "INT(10) COMMENT '表格最大高度'")
    private Integer maxHeight;
    @Schema(description = "初始化分页大小")
    @TableField(value = "INIT_PAGE_SIZE")
    @Column(name = "INIT_PAGE_SIZE", columnDefinition = "INT(10) COMMENT '初始化分页大小'")
    private Integer initPageSize;
    @Schema(description = "是否启用（true-是；false-否）")
    @TableField(value = "ENABLED")
    @Column(name = "ENABLED", columnDefinition = "BIT(1) COMMENT '是否启用（true-是；false-否）'")
    private Boolean enabled;
    @Schema(description = "是否允许单元格列宽拖动（true-是；false-否）")
    @TableField(value = "RESIZE_FLAG")
    @Column(name = "RESIZE_FLAG", columnDefinition = "BIT(1) COMMENT '是否允许单元格列宽拖动（true-是；false-否）'")
    private Boolean resizeFlag;
    @Schema(description = "是否允许根据内容自动计算列宽（true-是；false-否）")
    @TableField(value = "AUTO_COLS_WIDTH_FLAG")
    @Column(name = "AUTO_COLS_WIDTH_FLAG", columnDefinition = "BIT(1) COMMENT '是否允许根据内容自动计算列宽（true-是；false-否）'")
    private Boolean autoColsWidthFlag;
    @Schema(description = "是否启用默认工具栏（true-是；false-否）")
    @TableField(value = "DEFAULT_TOOL_BAR_FLAG")
    @Column(name = "DEFAULT_TOOL_BAR_FLAG", columnDefinition = "BIT(1) COMMENT '是否启用默认工具栏（true-是；false-否）'")
    private Boolean defaultToolBarFlag;
}
