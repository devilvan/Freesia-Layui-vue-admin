package com.freesia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 给部门分配角色 值对象
 * @date 2024-07-16
 */
@Data
@Schema(description = "给部门分配角色 值对象")
public class DeptAssignRoleVo {
    @Schema(description = "部门ID")
    private Long deptId;
    @Schema(description = "分配后的角色ID集合")
    private Set<Long> afterRoleIdSet;
}
