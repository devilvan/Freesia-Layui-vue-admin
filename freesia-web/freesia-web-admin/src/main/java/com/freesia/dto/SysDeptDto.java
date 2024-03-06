package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
* @author Evad.Wu
* @Description 部门信息表 数据传输对象
* @date 2023-08-12
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门信息表 数据传输对象")
public class SysDeptDto extends BaseDto {
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
    @Schema(description = "部门状态（1-是，0-否）")
    private String deptStatus;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间（从）")
    private Date createTimeFrom;
    @Schema(description = "创建时间（从）")
    private Date createTimeTo;
}
