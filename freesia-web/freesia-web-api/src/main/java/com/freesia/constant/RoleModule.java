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
        /**
         * 子模块 分配部门
         */
        public static final String ASSIGN_DEPT = "assign_dept";
        /**
         * 子模块 分配用户
         */
        public static final String ASSIGN_USER = "assign_user";

        /**
         * 子模块 取消分配用户
         */
        public static final String CANCEL_ASSIGN_USER = "cancel_assign_user";
        /**
         * 子模块 保存用户
         */
        public static final String SAVE_ROLE = "save_role";
        /**
         * 子模块 删除用户
         */
        public static final String DELETE_ROLE = "delete_role";
    }
}
