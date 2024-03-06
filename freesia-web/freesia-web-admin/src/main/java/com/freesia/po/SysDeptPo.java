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
 * @Description 部门信息表 映射
 * @date 2023-08-11
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_DEPT")

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "SYS_DEPT")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "部门信息表 映射")
public class SysDeptPo extends BasePo implements Serializable {
    @Serial
    private static final long serialVersionUID = -2398093865717242033L;
    @Schema(description = "父部门ID")
    @TableField(value = "PARENT_ID")
    @Column(name = "PARENT_ID", columnDefinition = "BIGINT(19) COMMENT '父部门ID'")
    private Long parentId;
    @Schema(description = "祖级列表")
    @TableField(value = "ANCESTORS")
    @Column(name = "ANCESTORS", columnDefinition = "VARCHAR(500) COMMENT '祖级列表'")
    private String ancestors;
    @Schema(description = "部门名称")
    @TableField(value = "DEPT_NAME")
    @Column(name = "DEPT_NAME", columnDefinition = "VARCHAR(30) COMMENT '部门名称'")
    private String deptName;
    @Schema(description = "显示顺序")
    @TableField(value = "ORDER_NUM")
    @Column(name = "ORDER_NUM", columnDefinition = "INT(10) COMMENT '显示顺序'")
    private Integer orderNum;
    @Schema(description = "负责人")
    @TableField(value = "LEADER")
    @Column(name = "LEADER", columnDefinition = "VARCHAR(20) COMMENT '负责人'")
    private String leader;
    @Schema(description = "联系电话")
    @TableField(value = "TEL_NO")
    @Column(name = "TEL_NO", columnDefinition = "VARCHAR(11) COMMENT '联系电话'")
    private String telNo;
    @Schema(description = "邮箱")
    @TableField(value = "EMAIL")
    @Column(name = "EMAIL", columnDefinition = "VARCHAR(50) COMMENT '邮箱'")
    private String email;
    @Schema(description = "部门状态（1-是，0-否）")
    @TableField(value = "DEPT_STATUS")
    @Column(name = "DEPT_STATUS", columnDefinition = "CHAR(1) COMMENT '部门状态（1-是，0-否）'")
    private String deptStatus;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(500) COMMENT '备注'")
    private String remark;

    @Schema(description = "部门对应的角色")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @ManyToMany(mappedBy = "sysDeptPoSet", fetch = FetchType.LAZY)
    private Set<SysRolePo> sysRolePoSet;
    @Schema(description = "部门对应的用户")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @TableField(exist = false)
    @OneToMany(targetEntity = SysUserPo.class, mappedBy = "sysDeptPo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SysUserPo> sysUserPoSet;
}
