package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 模块 静态类
 * @date 2023-09-13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SysModule {
    /* 用户管理模块*/
    /**
     * 主模块 用户管理模块
     */
    public static final String USER_MANAGEMENT = "user_management";
    /**
     * 子模块 登录
     */
    public static final String LOGIN = "login";
    /**
     * 子模块 登出
     */
    public static final String LOGOUT = "logout";
    /**
     * 子模块 注册
     */
    public static final String REGISTER = "register";
    /**
     * 子模块 密码验证
     */
    public static final String CHECK_PASSWORD = "check_password";
    /**
     * 子模块 校验验证码
     */
    public static final String CHECK_CAPTCHA = "check_captcha";
    /* 用户管理模块*/

    /* 角色管理模块*/
    /**
     * 主模块 角色管理模块
     */
    public static final String ROLE_MANAGEMENT = "role_management";
    /**
     * 子模块 分配角色权限
     */
    public static final String ASSIGN_ROLE_PERMISSIONS = "assign_role_permissions";
    /* 角色管理模块*/

    /* 菜单管理模块*/
    /**
     * 主模块 菜单管理模块
     */
    public static final String MENU_MANAGEMENT = "menu_management";
    /**
     * 子模块 分配菜单权限
     */
    public static final String ASSIGN_MENU_PERMISSIONS = "assign_menu_permissions";
    /**
     * 子模块 分配角色
     */
    public static final String ASSIGN_ROLE = "assign_role";
    /* 角色管理模块*/

    /* 字典管理*/
    /**
     * 主模块 字典管理模块
     */
    public static final String DICT_MANAGEMENT = "dict_management";
    /* 字典管理*/

    /* 面板管理*/
    /**
     * 主模块 面板管理
     */
    public static final String DASHBOARD_MANAGEMENT = "dashboard_management";
    /* 面板管理*/

}
