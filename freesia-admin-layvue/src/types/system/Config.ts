import {BaseEntity, BaseVo} from "../Common";

/**
 * 系统配置项 枚举类
 */
export enum SysConfigKey {
    /**
     * 账号自助-验证码开关（boolean）
     */
    SYS_ACCOUNT_CAPTCHA_ENABLED = "sys.account.captchaEnabled",
    /**
     * 首页图标URL
     */
    HOME_ICON_URL = "home.icon.url",
    /**
     * Gitee请求提交更新接口URL
     */
    GITEE_COMMIT_URL = "gitee.commit.url",
    /**
     * 记账导入模板
     */
    ACCOUNT_IMPORT_TEMPLATE_URL = "account.import.template.url"
}

export interface SysConfigVo extends BaseVo {
    configName?: string;
    configKey?: string;
    configValue?: string;
}

export interface SysConfigEntity extends BaseEntity {
    configName?: string;
    configKey?: string;
    configValue?: string;
}
