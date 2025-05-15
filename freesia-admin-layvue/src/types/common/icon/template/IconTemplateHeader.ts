import {saveUpdate} from "@/api/common/icon/Icon";
import {SysOssEntity} from "@/types/system/Oss";
import {BaseEntity, BaseVo} from "@/types/Common";

export interface CommonIconTemplateHeaderVo extends BaseVo {
    name?: string;
    orderNum?: number;
    remark?: string;
    userId?: string;
    defaultFlag?: boolean;
}

export interface CommonIconTemplateHeaderEntity extends BaseEntity {
    name?: string;
    orderNum?: number;
    remark?: string;
    userId?: string;
    defaultFlag?: boolean;
}