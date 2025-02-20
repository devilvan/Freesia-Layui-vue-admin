package com.freesia.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 根据部门ID查询【分配角色】加载数据 持久层传输类
 * {@link com.freesia.controller.SysDeptController#findDeptRolesByDeptId}
 * @date 2024-07-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FindDeptRolesByDeptIdEntity extends BaseEntity {
    @Schema(description = "部门ID")
    private Long deptId;
    @Schema(description = "部门名")
    private String deptName;
    @Schema(description = "部门对应的角色信息")
    private Set<Long> selectedRoles;
}
