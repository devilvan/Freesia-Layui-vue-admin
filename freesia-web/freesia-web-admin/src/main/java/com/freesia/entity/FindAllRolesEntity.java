package com.freesia.entity;

import com.freesia.controller.SysRoleController;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 查询所有角色 持久层传输类
 * {@link SysRoleController#findAllRoles()}
 * @date 2023-11-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindAllRolesEntity extends BaseEntity {
    @Schema(description = "角色ID")
    private Long id;
    @Schema(description = "角色键名")
    private String roleKey;
    @Schema(description = "角色名")
    private String roleName;
    @Schema(description = "数据范围")
    private String dataScope;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "备注")
    private String remark;
}
