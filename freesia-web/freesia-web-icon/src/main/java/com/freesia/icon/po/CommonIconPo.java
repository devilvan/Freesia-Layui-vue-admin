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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 通用图标表 映射
 * @date 2025-03-21
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "COMMON_ICON")

@Entity
@Table(name = "COMMON_ICON")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "通用图标表 映射")
public class CommonIconPo extends BasePo implements Serializable {
    @Schema(description = "图标名称")
    @TableField(value = "NAME")
    @Column(name = "NAME", columnDefinition = "VARCHAR(128) NOT NULL COMMENT '图标名称'")
    private String name;
    @Schema(description = "文件ID")
    @TableField(value = "FILE_ID")
    @Column(name = "FILE_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '文件ID'")
    private Long fileId;
    @Schema(description = "图标所属分区")
    @TableField(value = "ICON_PARTITION")
    @Column(name = "ICON_PARTITION", columnDefinition = "VARCHAR(32) COMMENT '图标所属分区'")
    private String iconPartition;
    @Schema(description = "排序")
    @TableField(value = "ORDER_NUM")
    @Column(name = "ORDER_NUM", columnDefinition = "INT(10) COMMENT '排序'")
    private Integer orderNum;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(128) COMMENT '备注'")
    private String remark;
}
