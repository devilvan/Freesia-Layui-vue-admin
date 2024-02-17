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
import java.util.HashSet;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 目录/菜单/按钮信息表 映射
 * @date 2023-08-11
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_MENU")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SYS_MENU")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "目录/菜单/按钮信息表 映射")
public class SysMenuPo extends BasePo implements Serializable, Comparable<SysMenuPo> {
    @Serial
    private static final long serialVersionUID = -1147129186425810209L;
    @Schema(description = "菜单名称")
    @TableField(value = "MENU_NAME")
    @Column(name = "MENU_NAME", columnDefinition = "VARCHAR(50) NOT NULL COMMENT '菜单名称'")
    private String menuName;
    @Schema(description = "父菜单ID")
    @TableField(value = "PARENT_ID")
    @Column(name = "PARENT_ID", columnDefinition = "BIGINT(19) COMMENT '父菜单ID'")
    private Long parentId;
    @Schema(description = "显示顺序")
    @TableField(value = "ORDER_NUM")
    @Column(name = "ORDER_NUM", columnDefinition = "INT(10) COMMENT '显示顺序'")
    private Integer orderNum;
    @Schema(description = "路由地址")
    @TableField(value = "PATH")
    @Column(name = "PATH", columnDefinition = "VARCHAR(200) COMMENT '路由地址'")
    private String path;
    @Schema(description = "组件路径")
    @TableField(value = "COMPONENT")
    @Column(name = "COMPONENT", columnDefinition = "VARCHAR(255) COMMENT '组件路径'")
    private String component;
    @Schema(description = "路由参数")
    @TableField(value = "QUERY_PARAM")
    @Column(name = "QUERY_PARAM", columnDefinition = "VARCHAR(255) COMMENT '路由参数'")
    private String queryParam;
    @Schema(description = "是否为外链（0-是 1-否）")
    @TableField(value = "IS_FRAME")
    @Column(name = "IS_FRAME", columnDefinition = "CHAR(1) COMMENT '是否为外链（0-是 1-否）'")
    private String isFrame;
    @Schema(description = "是否缓存（0-缓存 1-不缓存）")
    @TableField(value = "IS_CACHE")
    @Column(name = "IS_CACHE", columnDefinition = "CHAR(1) COMMENT '是否缓存（0-缓存 1-不缓存）'")
    private String isCache;
    @Schema(description = "菜单类型（见MENU_TYPE）")
    @TableField(value = "MENU_TYPE")
    @Column(name = "MENU_TYPE", columnDefinition = "CHAR(1) COMMENT '菜单类型（见MENU_TYPE）'")
    private String menuType;
    @Schema(description = "显示状态（0显示 1隐藏）")
    @TableField(value = "VISIBLE")
    @Column(name = "VISIBLE", columnDefinition = "CHAR(1) COMMENT '显示状态（0显示 1隐藏）'")
    private String visible;
    @Schema(description = "菜单状态（0正常 1停用）")
    @TableField(value = "STATUS")
    @Column(name = "STATUS", columnDefinition = "CHAR(1) COMMENT '菜单状态（0正常 1停用）'")
    private String status;
    @Schema(description = "权限标识")
    @TableField(value = "PERMS")
    @Column(name = "PERMS", columnDefinition = "VARCHAR(100) COMMENT '权限标识'")
    private String perms;
    @Schema(description = "菜单图标")
    @TableField(value = "ICON")
    @Column(name = "ICON", columnDefinition = "VARCHAR(100) COMMENT '菜单图标'")
    private String icon;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(128) COMMENT '备注'")
    private String remark;

    @Schema(description = "菜单对应的角色")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToMany(mappedBy = "sysMenuPoSet", fetch = FetchType.LAZY)
    private Set<SysRolePo> sysRolePoSet = new HashSet<>(0);

    @Schema(description = "菜单在菜单-角色关系表中的数据")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @OneToMany(targetEntity = SysRoleMenuPo.class, mappedBy = "sysMenuPo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SysRoleMenuPo> sysRoleMenuPoSet = new HashSet<>(0);

    @Override
    public int compareTo(SysMenuPo sysMenuPo) {
        return (int) (sysMenuPo.getId() - getId());
    }
}
