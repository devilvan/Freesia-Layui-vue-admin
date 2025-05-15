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
 * @Description 通用图标模板头表 映射
 * @date 2025-05-15
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "COMMON_ICON_TEMPLATE_HEADER")

@Entity
@Table(name = "COMMON_ICON_TEMPLATE_HEADER")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "通用图标模板头表 映射")
public class CommonIconTemplateHeaderPo extends BasePo implements Serializable {
    @Schema(description = "模板名称")
    @TableField(value = "NAME")
    @Column(name = "NAME", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '模板名称'")
    private String name;
    @Schema(description = "排序")
    @TableField(value = "ORDER_NUM")
    @Column(name = "ORDER_NUM", columnDefinition = "INT(10) COMMENT '排序'")
    private Integer orderNum;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(128) COMMENT '备注'")
    private String remark;
    @Schema(description = "所属用户ID")
    @TableField(value = "USER_ID")
    @Column(name = "USER_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '所属用户ID'")
    private Long userId;
    @Schema(description = "默认标识")
    @TableField(value = "DEFAULT_FLAG")
    @Column(name = "DEFAULT_FLAG", columnDefinition = "BIT(1) COMMENT '默认标识'")
    private Boolean defaultFlag;
}
