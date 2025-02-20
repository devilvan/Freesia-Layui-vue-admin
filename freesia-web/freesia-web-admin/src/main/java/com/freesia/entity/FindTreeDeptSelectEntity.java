package com.freesia.entity;

import com.freesia.controller.SysDeptController;
import com.freesia.dto.TreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 查询部门树下拉框 持久层传输类
 * {@link SysDeptController#findTreeDeptSelect}
 * @date 2024-07-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindTreeDeptSelectEntity extends TreeDto<FindTreeDeptSelectEntity> {
    @Schema(description = "部门ID")
    private Long id;
    @Schema(description = "上级部门ID")
    private Long parentId;
    @Schema(description = "部门名称")
    private String title;

}
