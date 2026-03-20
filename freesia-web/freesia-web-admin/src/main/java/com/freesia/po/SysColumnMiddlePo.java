package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freesia.po.BasePo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 系统列中间表 映射
 * @date 2026-03-20
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
    @Column(name = "ENABLED", columnDefinition = "INT(10) COMMENT '是否可用（true-是；false-否）'")
    private Integer enabled;

    @Schema(description = "系统列中间表在头-中间表关系中的系统列头数据")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysColumnHeaderPo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SysColumnHeaderPo sysColumnHeaderPo;

    @Schema(description = "系统列中间表在中间表-明细关系中的系统列明细数据")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @OneToMany(targetEntity = SysColumnDetailPo.class, mappedBy = "sysColumnMiddlePo", fetch = FetchType.LAZY)
    private Set<SysColumnDetailPo> sysColumnDetailPoSet;
}
