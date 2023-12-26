package com.freesia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 给用户分配角色 值对象
 * @date 2023-11-29
 */
@Data
@Schema(description = "给用户分配角色 值对象")
public class AssignRoleVo {
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "分配后的角色ID集合")
    private Set<Long> afterRoleIdSet;
}
