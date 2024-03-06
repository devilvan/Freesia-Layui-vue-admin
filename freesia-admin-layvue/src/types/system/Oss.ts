import {BaseEntity, BaseVo} from "../Common";

export interface SysOssVo extends BaseVo {
    fileName?: string;
    originalName?: string;
    fileSuffix?: string;
    url?: string;
    service?: string;
}

export interface SysOssEntity extends BaseEntity {
    fileName?: string;
    originalName?: string;
    fileSuffix?: string;
    url?: string;
    service?: string;
}
