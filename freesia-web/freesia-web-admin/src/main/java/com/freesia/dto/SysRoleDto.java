package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 角色信息表 数据传输对象
 * @date 2023-08-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色信息表 数据传输对象")
public class SysRoleDto extends BaseDto {

    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "角色权限字符串")
    private String roleKey;
    @Schema(description = "角色状态（0正常 1停用）")
    private String status;
    @Schema(description = "显示顺序")
    private Integer orderNum;
    @Schema(description = "数据范围（见DATA_SCOPE）")
    private String dataScope;
    @Schema(description = "菜单树选择项是否关联显示")
    private Boolean menuCheckStrictly;
    @Schema(description = "部门树选择项是否关联显示")
    private Boolean deptCheckStrictly;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间（从）")
    private Date createTimeFrom;
    @Schema(description = "创建时间（从）")
    private Date createTimeTo;
}
