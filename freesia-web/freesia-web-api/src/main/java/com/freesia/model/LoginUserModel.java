package com.freesia.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 登录用户身份权限对象 通用模型类
 * @date 2023-08-17
 */
@Data
public class LoginUserModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1058458708469758769L;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "部门ID")
    private Long deptId;
    @Schema(description = "部门名")
    private String deptName;
    @Schema(description = "用户唯一标识")
    private String token;
    @Schema(description = "用户类型")
    private String userType;
    @Schema(description = "登录时间")
    private Long loginTime;
    @Schema(description = "过期时间")
    private Long expireTime;
    @Schema(description = "登录IP地址")
    private String ipaddr;
    @Schema(description = "登录地点")
    private String loginLocation;
    @Schema(description = "浏览器类型")
    private String browser;
    @Schema(description = "操作系统")
    private String os;
    @Schema(description = "菜单权限")
    private Set<String> menuPermission;
    @Schema(description = "角色权限")
    private Set<String> rolePermission;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "角色对象")
    private List<SysRoleModel> roles;
    @Schema(description = "数据权限 当前角色ID")
    private Long roleId;

    /**
     * 获取登录ID
     *
     * @return 登录ID
     */
    public String getLoginId() {
        if (userType == null) {
            throw new IllegalArgumentException("用户类型不能为空");
        }
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userType + ":" + userId;
    }
}
