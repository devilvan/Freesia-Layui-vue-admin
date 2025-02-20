package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 登录模块 静态类
 * @date 2024-01-13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserModule extends SysModule {
    /**
     * 主模块 用户管理模块
     */
    public static final String USER_MANAGEMENT = "user_management";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SubModule {
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
        /**
         * 子模块 校验验证码
         */
        public static final String USER_IMPORT = "user_import";
        /**
         * 子模块 分配部门
         */
        public static final String ASSIGN_DEPT = "assign_dept";
        /**
         * 子模块 用户头像修改
         */
        public static final String AVATAR_UPDATE = "avatar_update";
        /**
         * 子模块 删除用户
         */
        public static final String DELETE_USER = "delete_user";

    }
}
