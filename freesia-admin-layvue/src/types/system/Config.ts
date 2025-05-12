import {BaseEntity, BaseVo} from "../Common";

/**
 * 系统配置项 枚举类
 */
export enum SysConfigKey {
    /**
     * 首页图标URL
     */
    HOME_ICON_URL = "home.icon.url",
    /**
     * 添加图标URL
     */
    ADD_ICON_URL = "add.icon.url"
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
