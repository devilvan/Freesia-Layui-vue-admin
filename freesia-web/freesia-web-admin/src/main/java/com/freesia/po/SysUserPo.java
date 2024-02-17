package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 用户信息表 映射
 * @date 2023-08-11
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "SYS_USER")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SYS_USER")
@Schema(description = "用户信息表 映射")
public class SysUserPo extends BasePo implements Serializable {
    @Serial
    private static final long serialVersionUID = -3634012152235978776L;
    @Schema(description = "部门ID")
    @TableField(value = "DEPT_ID")
    @Column(name = "DEPT_ID", columnDefinition = "BIGINT(19) COMMENT '部门ID'")
    private Long deptId;
    @Schema(description = "用户账号")
    @TableField(value = "USER_NAME")
    @Column(name = "USER_NAME", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '用户账号'")
    private String userName;
    @Schema(description = "用户昵称")
    @TableField(value = "NICK_NAME")
    @Column(name = "NICK_NAME", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '用户昵称'")
    private String nickName;
    @Schema(description = "用户类型（见USER_TYPE）")
    @TableField(value = "USER_TYPE")
    @Column(name = "USER_TYPE", columnDefinition = "VARCHAR(10) COMMENT '用户类型（见USER_TYPE）'")
    private String userType;
    @Schema(description = "用户邮箱")
    @TableField(value = "EMAIL")
    @Column(name = "EMAIL", columnDefinition = "VARCHAR(50) COMMENT '用户邮箱'")
    private String email;
    @Schema(description = "手机号码")
    @TableField(value = "TEL_NO")
    @Column(name = "TEL_NO", columnDefinition = "VARCHAR(11) COMMENT '手机号码'")
    private String telNo;
    @Schema(description = "用户性别（M-男 F-女 U-未知）")
    @TableField(value = "GENDER")
    @Column(name = "GENDER", columnDefinition = "CHAR(8) COMMENT '用户性别（M-男 F-女 U-未知）'")
    private String gender;
    @Schema(description = "头像地址")
    @TableField(value = "AVATAR")
    @Column(name = "AVATAR", columnDefinition = "VARCHAR(100) COMMENT '头像地址'")
    private String avatar;
    @Schema(description = "密码")
    @TableField(value = "PASSWORD")
    @Column(name = "PASSWORD", columnDefinition = "VARCHAR(100) COMMENT '密码'")
    private String password;
    @Schema(description = "帐号状态（见ACCOUNT_STATUS）")
    @TableField(value = "ACCOUNT_STATUS")
    @Column(name = "ACCOUNT_STATUS", columnDefinition = "CHAR(1) COMMENT '帐号状态（见ACCOUNT_STATUS）'")
    private String accountStatus;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(500) COMMENT '备注'")
    private String remark;

    @Schema(description = "用户对应的角色")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToMany(mappedBy = "sysUserPoSet", fetch = FetchType.LAZY)
    private Set<SysRolePo> sysRolePoSet = new HashSet<>(0);
    @Schema(description = "用户对应的部门")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToOne(targetEntity = SysDeptPo.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private SysDeptPo sysDeptPo;
    @Schema(description = "用户在用户-角色关系表中的数据")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @OneToMany(targetEntity = SysUserRolePo.class, mappedBy = "sysUserPo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SysUserRolePo> sysUserRolePoSet = new HashSet<>(0);
}
