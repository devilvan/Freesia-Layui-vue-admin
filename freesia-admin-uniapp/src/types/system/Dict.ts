import {BaseEntity, BaseVo} from "../Common";

export interface SysDictValueVo extends BaseVo {
    dictType?: string;
    dictLabel?: string;
    dictValue?: string;
    isDefault?: boolean;
    sort?: number;
    remark?: string;
}

export interface SysDictValueEntity extends BaseEntity {
    dictType?: string;
    dictLabel?: string;
    dictValue?: string;
    isDefault?: boolean;
    sort?: number;
    remark?: string;
    value?: string;
    label?: string;
    defaultFlag?: boolean;
}
