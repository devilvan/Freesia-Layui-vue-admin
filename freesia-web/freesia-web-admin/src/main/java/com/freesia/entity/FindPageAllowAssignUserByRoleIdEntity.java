package com.freesia.entity;

import com.freesia.dto.TreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 查询用户信息和已分配该角色的用户列表 持久层传输对象
 * {@link com.freesia.controller.SysRoleController#findPageUserByRoleId}
 * @date 2023-09-05
 */
@Data
public class FindPageAllowAssignUserByRoleIdEntity {
    private String userId;
    @Schema(description = "用户账号")
    private String userName;
    @Schema(description = "用户昵称")
    private String nickName;
    @Schema(description = "用户类型（见USER_TYPE）")
    private String userType;
    @Schema(description = "帐号状态（见ACCOUNT_STATUS）")
    private String accountStatus;
    @Schema(description = "备注")
    private String remark;
}
