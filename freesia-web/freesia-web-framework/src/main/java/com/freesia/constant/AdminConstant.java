package com.freesia.constant;

import lombok.AccessLevel;
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

    /* SYS_CONFIG */
    /**
     * 主框架页-默认皮肤样式名称
     */
    public static final String SYS_INDEX_SKIN_NAME = "sys.index.skinName";
    /**
     * 用户管理-账号初始密码
     */
    public static final String SYS_USER_INIT_PASSWORD = "sys.user.initPassword";
    /**
     * 主框架页-侧边栏主题
     */
    public static final String SYS_INDEX_SIDE_THEME = "sys.index.sideTheme";
    /**
     * 账号自助-验证码开关
     */
    public static final String SYS_ACCOUNT_CAPTCHA_ENABLED = "sys.account.captchaEnabled";
    /**
     * 账号自助-是否开启用户注册功能
     */
    public static final String SYS_ACCOUNT_REGISTER_USER = "sys.account.registerUser";
    /**
     * OSS预览列表资源开关
     */
    public static final String SYS_OSS_PREVIEW_LIST_RESOURCE = "sys.oss.previewListResource";
    /* SYS_CONFIG */
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
     * 窗口打开
     */
    public static final String MODAL = "modal";
    /**
     * 新建标签页
     */
    public static final String BLANK = "blank";
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
}
