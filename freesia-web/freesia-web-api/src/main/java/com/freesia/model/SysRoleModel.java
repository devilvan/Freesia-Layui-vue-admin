package com.freesia.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 角色信息 通用模型类
 * @date 2023-08-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色信息 通用模型类")
public class SysRoleModel extends BaseModel {
    @Serial
    private static final long serialVersionUID = 432516547672290661L;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "角色权限字符串")
    private String roleKey;
    @Schema(description = "角色状态（0-停用，1-正常）")
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
}
