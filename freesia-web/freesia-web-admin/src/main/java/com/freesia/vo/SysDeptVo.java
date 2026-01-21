package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 部门信息表 值对象
 * @date 2023-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "部门信息表 值对象")
public class SysDeptVo extends BaseVo {
    @Schema(description = "父部门ID")
    @JsonAlias(value = {"parentId"})
    private Long parentId;
    @Schema(description = "祖级列表")
    @JsonAlias(value = {"ancestors"})
    private String ancestors;
    @Schema(description = "部门名称")
    @JsonAlias(value = {"deptName"})
    private String deptName;
    @Schema(description = "显示顺序")
    @JsonAlias(value = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "负责人")
    @JsonAlias(value = {"leader"})
    private String leader;
    @Schema(description = "联系电话")
    @JsonAlias(value = {"telNo"})
    private String telNo;
    @Schema(description = "邮箱")
    @JsonAlias(value = {"email"})
    private String email;
    @Schema(description = "部门状态（0-禁用，1-启用）")
    @JsonAlias(value = {"deptStatus"})
    private String deptStatus;
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
    @Schema(description = "创建时间（从）")
    private Date createTimeFrom;
    @Schema(description = "创建时间（从）")
    private Date createTimeTo;
}
