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
 * @Description 角色-菜单关联表 映射
 * @date 2023-10-20
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_ROLE_MENU")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SYS_ROLE_MENU")
@Schema(description = "角色-菜单关联表 映射")
public class SysRoleMenuPo extends RelationPo {
    @Serial
    private static final long serialVersionUID = 8813520811387381993L;
    @EmbeddedId
    @Schema(description = "角色-菜单 联合主键")
    protected SysRoleMenuPk sysRoleMenuPk;

    @Schema(description = "菜单-角色关系表对应的菜单")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysMenuPo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SysMenuPo sysMenuPo;

    @Schema(description = "菜单-角色关系表对应的角色")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysRolePo.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SysRolePo sysRolePo;

    public SysRoleMenuPo(SysRoleMenuPk sysRoleMenuPk) {
        this.sysRoleMenuPk = sysRoleMenuPk;
    }
}


