package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 角色-部门关联表 映射
 * @date 2023-10-20
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_ROLE_DEPT")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SYS_ROLE_DEPT")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "角色-部门关联表 映射")
public class SysRoleDeptPo extends RelationPo {
    @Serial
    private static final long serialVersionUID = 3381942492949819568L;
    @EmbeddedId
    @Schema(description = "角色-部门 联合主键")
    protected SysRoleDeptPk sysRoleDeptPk;
    @Schema(description = "部门-角色关系表对应的角色")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysRolePo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SysRolePo sysRolePo;
    @Schema(description = "部门-角色关系表对应的部门")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysDeptPo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SysDeptPo sysDeptPo;

    public SysRoleDeptPo(SysRoleDeptPk sysRoleDeptPk) {
        this.sysRoleDeptPk = sysRoleDeptPk;
    }
}

