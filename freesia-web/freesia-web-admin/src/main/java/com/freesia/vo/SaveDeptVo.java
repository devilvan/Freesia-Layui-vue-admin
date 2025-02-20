package com.freesia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 保存部门信息 值对象
 * @date 2024-07-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "保存部门信息 值对象")
public class SaveDeptVo extends BaseVo {
    @Schema(description = "父部门ID")
    private Long parentId;
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
    @Schema(description = "部门状态（0-禁用，1-启用）")
    private String deptStatus;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间（从）")
    private Date createTimeFrom;
    @Schema(description = "创建时间（从）")
    private Date createTimeTo;
}
