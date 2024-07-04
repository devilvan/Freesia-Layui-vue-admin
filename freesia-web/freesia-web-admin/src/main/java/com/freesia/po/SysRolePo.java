package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 角色信息表 映射
 * @date 2023-08-11
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_ROLE")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SYS_ROLE")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "角色信息表 映射")
public class SysRolePo extends BasePo implements Serializable {
    @Serial
    private static final long serialVersionUID = -5506566851317765658L;
    @Schema(description = "角色名称")
    @TableField(value = "ROLE_NAME")
    @Column(name = "ROLE_NAME", columnDefinition = "VARCHAR(30) NOT NULL COMMENT '角色名称'")
    private String roleName;
    @Schema(description = "角色权限字符串")
    @TableField(value = "ROLE_KEY")
    @Column(name = "ROLE_KEY", columnDefinition = "VARCHAR(100) NOT NULL COMMENT '角色权限字符串'")
    private String roleKey;
    @Schema(description = "角色状态（0-停用，1-正常）")
    @TableField(value = "STATUS")
    @Column(name = "STATUS", columnDefinition = "CHAR(1) NOT NULL COMMENT '角色状态（0-停用，1-正常）'")
    private String status;
    @Schema(description = "显示顺序")
    @TableField(value = "ORDER_NUM")
    @Column(name = "ORDER_NUM", columnDefinition = "INT(10) NOT NULL COMMENT '显示顺序'")
    private Integer orderNum;
    @Schema(description = "数据范围（见DATA_SCOPE）")
    @TableField(value = "DATA_SCOPE")
    @Column(name = "DATA_SCOPE", columnDefinition = "CHAR(1) COMMENT '数据范围（见DATA_SCOPE）'")
    private String dataScope;
    @Schema(description = "菜单树选择项是否关联显示")
    @TableField(value = "MENU_CHECK_STRICTLY")
    @Column(name = "MENU_CHECK_STRICTLY", columnDefinition = "BIT(1) COMMENT '菜单树选择项是否关联显示'")
    private Boolean menuCheckStrictly;
    @Schema(description = "部门树选择项是否关联显示")
    @TableField(value = "DEPT_CHECK_STRICTLY")
    @Column(name = "DEPT_CHECK_STRICTLY", columnDefinition = "BIT(1) COMMENT '部门树选择项是否关联显示'")
    private Boolean deptCheckStrictly;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(500) COMMENT '备注'")
    private String remark;

    @Schema(description = "角色对应的用户")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToMany(targetEntity = SysUserPo.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "SYS_USER_ROLE",
            joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    @Fetch(value = FetchMode.SUBSELECT)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<SysUserPo> sysUserPoSet;
    @Schema(description = "角色对应的部门")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToMany(targetEntity = SysDeptPo.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "SYS_ROLE_DEPT",
            joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "DEPT_ID", referencedColumnName = "ID")})
    private Set<SysDeptPo> sysDeptPoSet;
    @Schema(description = "角色对应的菜单")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToMany(targetEntity = SysMenuPo.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "SYS_ROLE_MENU",
            joinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "MENU_ID", referencedColumnName = "ID")})
    @Fetch(value = FetchMode.SUBSELECT)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<SysMenuPo> sysMenuPoSet;

    @Schema(description = "角色在菜单-角色关系表中的数据")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @OneToMany(targetEntity = SysRoleMenuPo.class, mappedBy = "sysRolePo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SysRoleMenuPo> sysRoleMenuPoSet = new HashSet<>(0);
    @Schema(description = "角色在用户-角色关系表中的数据")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @OneToMany(targetEntity = SysUserRolePo.class, mappedBy = "sysRolePo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SysUserRolePo> sysUserRolePoSet = new HashSet<>(0);
}
