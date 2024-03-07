package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 字典值信息表 映射
 * @date 2023-09-09
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "SYS_DICT_VALUE")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SYS_DICT_VALUE")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "字典值信息表 映射")
public class SysDictValuePo extends BasePo implements Serializable {
    @Serial
    private static final long serialVersionUID = -8910449150530655892L;
    @Schema(description = "字典键ID")
    @TableField(value = "KEY_ID")
    @Column(name = "KEY_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '字典键ID'")
    private Long keyId;
    @Schema(description = "字典键名")
    @TableField(value = "DICT_KEY")
    @Column(name = "DICT_KEY", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '字典键名'")
    private String dictKey;
    @Schema(description = "字典值名")
    @TableField(value = "VALUE_NAME")
    @Column(name = "VALUE_NAME", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '字典值名'")
    private String valueName;
    @Schema(description = "字典值")
    @TableField(value = "VALUE")
    @Column(name = "VALUE", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '字典值'")
    private String value;
    @Schema(description = "内部排序值")
    @TableField(value = "ORDER_NUM")
    @Column(name = "ORDER_NUM", columnDefinition = "INT(10) NOT NULL COMMENT '内部排序值'")
    private Integer orderNum;
    @Schema(description = "是否默认（0-否，1-是）")
    @TableField(value = "IS_DEFAULT")
    @Column(name = "IS_DEFAULT", columnDefinition = "BIT(1) NOT NULL COMMENT '是否默认（0-否，1-是）'")
    private String isDefault;
    @Schema(description = "国际化展示编码")
    @TableField(value = "I18N")
    @Column(name = "I18N", columnDefinition = "VARCHAR(64) COMMENT '国际化展示编码'")
    private String i18n;
    @Schema(description = "前端展示样式（CSS）")
    @TableField(value = "CSS_STYLE")
    @Column(name = "CSS_STYLE", columnDefinition = "VARCHAR(128) COMMENT '前端展示样式（CSS）'")
    private String cssStyle;
    @Schema(description = "状态（0-禁用，1-启用）")
    @TableField(value = "STATUS")
    @Column(name = "STATUS", columnDefinition = "CHAR(1) NOT NULL COMMENT '状态（0-禁用，1-启用）'")
    private String status;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(500) COMMENT '备注'")
    private String remark;
    @Schema(description = "字典值对应的键")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysDictKeyPo.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumns(value = {
            @JoinColumn(name = "KEY_ID", referencedColumnName = "ID", insertable = false, updatable = false),
            @JoinColumn(name = "DICT_KEY", referencedColumnName = "DICT_KEY", insertable = false, updatable = false)
    })
    private SysDictKeyPo sysDictKeyPo;
}
