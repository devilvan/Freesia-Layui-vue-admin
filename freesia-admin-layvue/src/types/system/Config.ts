import {BaseEntity, BaseVo} from "../Common";

export interface SysConfigVo extends BaseVo {
    configName?: string;
    configKey?: string;
    configValue?: string;
    configType?: boolean | false;
}

export interface SysConfigEntity extends BaseEntity {
    configName?: string;
    configKey?: string;
    configValue?: string;
    configType?: string;
}
