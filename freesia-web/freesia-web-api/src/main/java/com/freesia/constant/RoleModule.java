package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 角色模块 静态类
 * @date 2024-01-13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleModule extends SysModule {
    /**
     * 主模块 角色管理模块
     */
    public static final String ROLE_MANAGEMENT = "role_management";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SubModule {
        /**
         * 子模块 分配角色权限
         */
        public static final String ASSIGN_ROLE_PERMISSIONS = "assign_role_permissions";
    }
}
