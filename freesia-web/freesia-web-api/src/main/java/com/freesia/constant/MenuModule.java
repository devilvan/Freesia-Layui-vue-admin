package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 菜单模块 静态类
 * @date 2024-01-13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuModule extends SysModule{
    /**
     * 主模块 菜单管理模块
     */
    public static final String MENU_MANAGEMENT = "menu_management";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SubModule {
        /**
         * 子模块 分配菜单权限
         */
        public static final String ASSIGN_MENU_PERMISSIONS = "assign_menu_permissions";
        /**
         * 子模块 分配角色
         */
        public static final String ASSIGN_ROLE = "assign_role";
    }
}
