package com.freesia.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 根据角色ID查询【分配部门】加载数据 持久层传输类
 * {@link com.freesia.controller.SysRoleController#findDeptRolesByRoleId}
 * @date 2024-07-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FindDeptRolesByRoleIdEntity extends BaseEntity {
    @Schema(description = "角色ID")
    private Long roleId;
    @Schema(description = "角色名")
    private String roleName;
    @Schema(description = "角色对应的部门信息")
    private Set<Long> selectedDept;
}
