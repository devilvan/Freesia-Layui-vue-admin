package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
* @author Evad.Wu
* @Description 部门信息表 值对象
* @date 2023-08-12
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "部门信息表 值对象")
public class SysDeptVo {
    @Schema(description = "父部门ID")
    @JSONField(alternateNames = {"parentId"})
    private Long parentId;
    @Schema(description = "祖级列表")
    @JSONField(alternateNames = {"ancestors"})
    private String ancestors;
    @Schema(description = "部门名称")
    @JSONField(alternateNames = {"deptName"})
    private String deptName;
    @Schema(description = "显示顺序")
    @JSONField(alternateNames = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "负责人")
    @JSONField(alternateNames = {"leader"})
    private String leader;
    @Schema(description = "联系电话")
    @JSONField(alternateNames = {"telNo"})
    private String telNo;
    @Schema(description = "邮箱")
    @JSONField(alternateNames = {"email"})
    private String email;
    @Schema(description = "部门状态（见DEPT_STATUS）")
    @JSONField(alternateNames = {"deptStatus"})
    private String deptStatus;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
    @Schema(description = "创建时间（从）")
    private Date createTimeFrom;
    @Schema(description = "创建时间（从）")
    private Date createTimeTo;
}
