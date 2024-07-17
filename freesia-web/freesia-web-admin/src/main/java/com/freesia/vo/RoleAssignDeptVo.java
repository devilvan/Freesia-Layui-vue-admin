package com.freesia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 给角色分配部门 值对象
 * @date 2024-07-17
 */
@Data
@Schema(description = "给角色分配部门 值对象")
public class RoleAssignDeptVo {
    @Schema(description = "角色ID")
    private Long roleId;
    @Schema(description = "待分配的部门ID集合")
    private Set<Long> deptIdList;
}
