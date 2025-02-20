package com.freesia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 给用户分配部门 值对象
 * @date 2024-07-12
 */
@Data
@Schema(description = "给用户分配部门 值对象")
public class AssignDeptVo {
    @Schema(description = "用户ID集合")
    private List<Long> userIdList;
    @Schema(description = "部门ID")
    private Long deptId;
}
