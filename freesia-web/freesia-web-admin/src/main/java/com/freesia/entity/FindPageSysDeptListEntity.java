package com.freesia.entity;

import com.freesia.dto.TreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 获取部门下拉树 持久层传输对象
 * {@link com.freesia.controller.SysDeptController#findPageSysDeptList}
 * @date 2023-09-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPageSysDeptListEntity extends TreeDto<FindPageSysDeptListEntity> {
    @Schema(description = "祖级列表")
    private String ancestors;
    @Schema(description = "部门名称")
    private String deptName;
    @Schema(description = "显示顺序")
    private Integer orderNum;
    @Schema(description = "负责人")
    private String leader;
    @Schema(description = "联系电话")
    private String telNo;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "部门状态（见DEPT_STATUS）")
    private String deptStatus;
    @Schema(description = "备注")
    private String remark;
}
