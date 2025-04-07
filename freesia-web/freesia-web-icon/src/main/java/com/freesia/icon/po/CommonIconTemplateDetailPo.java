package com.freesia.icon.po;

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
 * @Description 通用图标模板表 映射
 * @date 2025-04-07
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "COMMON_ICON_TEMPLATE_DETAIL")

@Entity
@Table(name = "COMMON_ICON_TEMPLATE_DETAIL")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "通用图标模板表 映射")
public class CommonIconTemplateDetailPo extends BasePo implements Serializable {
    @Schema(description = "图标ID")
    @TableField(value = "ICON_ID")
    @Column(name = "ICON_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '图标ID'")
    private Long iconId;
    @Schema(description = "自定义图标名称")
    @TableField(value = "NAME")
    @Column(name = "NAME", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '自定义图标名称'")
    private String name;
    @Schema(description = "自定义分组")
    @TableField(value = "GROUPING")
    @Column(name = "GROUPING", columnDefinition = "VARCHAR(32) COMMENT '自定义分组'")
    private String grouping;
    @Schema(description = "排序")
    @TableField(value = "ORDER_NUM")
    @Column(name = "ORDER_NUM", columnDefinition = "INT(10) COMMENT '排序'")
    private Integer orderNum;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(128) COMMENT '备注'")
    private String remark;
}
