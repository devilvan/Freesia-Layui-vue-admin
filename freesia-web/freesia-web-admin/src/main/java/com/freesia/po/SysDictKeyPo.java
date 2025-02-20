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
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 字典键信息表 映射
 * @date 2023-09-09
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_DICT_KEY")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SYS_DICT_KEY")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "字典键信息表 映射")
public class SysDictKeyPo extends BasePo implements Serializable {
    @Serial
    private static final long serialVersionUID = -7706152980398132046L;
    @Schema(description = "字典键名")
    @TableField(value = "KEY_NAME")
    @Column(name = "KEY_NAME", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '字典键名'")
    private String keyName;
    @Schema(description = "字典键")
    @TableField(value = "DICT_KEY")
    @Column(name = "DICT_KEY", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '字典键'")
    private String dictKey;
    @Schema(description = "状态（0-禁用，1-启用）")
    @TableField(value = "STATUS")
    @Column(name = "STATUS", columnDefinition = "CHAR(1) NOT NULL COMMENT '状态（0-禁用，1-启用）'")
    private String status;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(500) COMMENT '备注'")
    private String remark;
    @Schema(description = "字典键对应的值")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @OneToMany(targetEntity = SysDictValuePo.class, mappedBy = "sysDictKeyPo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SysDictValuePo> sysDictValuePoSet;

}
