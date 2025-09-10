package com.freesia.icon.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freesia.po.BasePo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 通用图标模板明细表 映射
 * @date 2025-05-15
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "COMMON_ICON_TEMPLATE_DETAIL")

@Entity
@Table(name = "COMMON_ICON_TEMPLATE_DETAIL")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "通用图标模板明细表 映射")
public class CommonIconTemplateDetailPo extends BasePo implements Serializable {
    @Schema(description = "自定义分组")
    @TableField(value = "GROUPING")
    @Column(name = "`GROUPING`", columnDefinition = "VARCHAR(32) COMMENT '自定义分组'")
    private String grouping;
    @Schema(description = "通用图标模板头表ID")
    @TableField(value = "HEADER_ID")
    @Column(name = "HEADER_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '通用图标模板头表ID'")
    private Long headerId;
    @Schema(description = "图标ID")
    @TableField(value = "ICON_ID")
    @Column(name = "ICON_ID", columnDefinition = "BIGINT(19) COMMENT '图标ID'")
    private Long iconId;
    @Schema(description = "节点类型（ICON_TREE_TYPE）")
    @TableField(value = "ICON_TREE_TYPE")
    @Column(name = "ICON_TREE_TYPE", columnDefinition = "CHAR(1) COMMENT '节点类型（ICON_TREE_TYPE）'")
    private String iconTreeType;
    @Schema(description = "自定义图标名称")
    @TableField(value = "NAME")
    @Column(name = "NAME", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '自定义图标名称'")
    private String name;
    @Schema(description = "排序")
    @TableField(value = "ORDER_NUM")
    @Column(name = "ORDER_NUM", columnDefinition = "INT(10) COMMENT '排序'")
    private Integer orderNum;
    @Schema(description = "父级ID")
    @TableField(value = "PARENT_ID")
    @Column(name = "PARENT_ID", columnDefinition = "BIGINT(19) COMMENT '父级ID'")
    private Long parentId;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(128) COMMENT '备注'")
    private String remark;

    @Schema(description = "图标模板明细在头-明细关系中的头数据")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = CommonIconTemplateHeaderPo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private CommonIconTemplateHeaderPo commonIconTemplateHeaderPo;
}
