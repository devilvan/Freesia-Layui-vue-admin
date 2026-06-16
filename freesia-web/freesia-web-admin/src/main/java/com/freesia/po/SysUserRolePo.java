package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 用户-角色关联表 映射
 * @date 2023-12-01
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_USER_ROLE")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SYS_USER_ROLE")
@Schema(description = "用户-角色关联表 映射")
public class SysUserRolePo extends RelationPo {
    @Serial
    private static final long serialVersionUID = 8813520811387381993L;
    @EmbeddedId
    @Schema(description = "用户-角色 联合主键")
    protected SysUserRolePk sysUserRolePk;

    @Schema(description = "用户-角色关系表对应的用户")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysUserPo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SysUserPo sysUserPo;

    @Schema(description = "用户-角色关系表对应的角色")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysRolePo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SysRolePo sysRolePo;
}



