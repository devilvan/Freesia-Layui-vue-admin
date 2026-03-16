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
 * @date 2026-03-16
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
    @Schema(description = "组件名")
    @TableField(value = "COMPONENT")
    @Column(name = "COMPONENT", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '组件名'")
    private String component;
    @Schema(description = "是否启用（true-是；false-否）")
    @TableField(value = "ENABLED")
    @Column(name = "ENABLED", columnDefinition = "BIT(1) COMMENT '是否启用（true-是；false-否）'")
    private Boolean enabled;
}
