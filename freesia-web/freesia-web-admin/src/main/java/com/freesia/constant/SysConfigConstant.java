package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 系统配置键 静态类
 * @date 2024-01-16
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SysConfigConstant {
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
    /**
     * Gitee请求提交更新接口URL
     */
    public static final String GITEE_COMMIT_URL = "gitee.commit.url";
}
