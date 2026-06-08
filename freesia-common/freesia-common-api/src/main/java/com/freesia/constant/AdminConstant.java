package com.freesia.constant;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 管理模块 静态类
 * @date 2023-08-11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AdminConstant {
    /**
     * 用户名最小长度
     */
    public static final int USERNAME_MIN_LENGTH = 2;
    /**
     * 用户名最大长度
     */
    public static final int USERNAME_MAX_LENGTH = 20;
    /**
     * 密码最小长度
     */
    public static final int PASSWORD_MIN_LENGTH = 6;
    /**
     * 密码最大长度
     */
    public static final int PASSWORD_MAX_LENGTH = 20;
    /**
     * 管理员ID
     */
    public static final long ADMIN_ID = 1L;
    /**
     * 系统对象
     */
    public static final String SYSTEM = "system";
    /**
     * 管理员
     */
    public static final String ADMIN = "admin";
    /**
     * 用户
     */
    public static final String USER = "user";
    /**
     *
     */
    public static final String ADMIN_MENU_PERMS = "*:*:*";

    /**
     * 未知
     */
    public static final String UNKNOWN = "unknown";
    /**
     * 注册
     */
    public static final String REGISTER = "Register";
    /**
     * 顶级菜单的父菜单ID
     */
    public static final Long MENU_TOP_PARENT_ID = -1L;
    /**
     * 顶级部门的父菜单ID
     */
    public static final Long DEPT_TOP_PARENT_ID = -1L;
    /**
     * 窗口打开
     */
    public static final String MODAL = "modal";
    /**
     * 新建标签页
     */
    public static final String BLANK = "blank";
    /**
     * 内容页打开
     */
    public static final String INNER = "innerLink";
    /**
     * 内容页打开，默认component
     */
    public static final String INNER_COMPONENT = "iframe/inner/index";


    /**
     * 基本组件标识
     */
    public static final String BASE_LAYOUT = "BaseLayout";
    /**
     * 空白组件标识
     */
    public static final String BLANK_LAYOUT = "BlankLayout";
    /**
     * 链接组件标识
     */
    public static final String INNER_LINK = "InnerLink";

    @Getter
    @AllArgsConstructor
    public static enum RoleKey {
        ADMIN("admin"),
        COMMON("common"),
        DEPT_UNDERLING("dept_underling"),
        OWN("own");

        /**
         * 编码
         */
        private final String code;

        /**
         * 根据编码获取实例
         *
         * @param code 编码
         * @return 实例
         */
        public static RoleKey getInstanceByCode(String code) {
            if (StrUtil.isEmpty(code)) {
                return null;
            }
            for (RoleKey roleKey : RoleKey.values()) {
                if (roleKey.getCode().equals(code)) {
                    return roleKey;
                }
            }
            return null;
        }
    }
}
