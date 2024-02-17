package com.freesia.vo;

import com.freesia.controller.SysRoleController;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 保存角色对应的菜单信息 值对象
 * {@link SysRoleController#saveRoleMenuPrivilege}
 * @date 2023-10-22
 */
@Data
@Schema(description = "保存角色对应的菜单信息 值对象")
public class SaveRoleMenuPrivilegeVo {
    @Schema(description = "角色ID")
    private Long roleId;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "数据范围")
    private String dataScope;
    @Schema(description = "选中菜单")
    private List<Long> treeSelectedIdList;
}
