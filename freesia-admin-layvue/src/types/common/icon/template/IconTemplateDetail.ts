import {saveUpdate} from "@/api/common/icon/Icon";
import {SysOssEntity} from "@/types/system/Oss";
import {BaseEntity, BaseVo} from "@/types/Common";
import {Tree} from "@/types/Result";

export enum IconTreeType {
    "R" = "R",
    "L" = "L",

}

export interface CommonIconTemplateDetailVo extends BaseVo {
    headerId?: string;
    name?: string;
    grouping?: string;
    orderNum?: number;
    remark?: string;
    iconTreeType?: string;
}

export interface CommonIconTemplateDetailEntity extends BaseEntity {
    headerId?: string;
    name?: string;
    grouping?: string;
    orderNum?: number;
    remark?: string;
    iconTreeType?: string;
}

export interface FindTreeIconTreeTypeEntity extends Tree<FindTreeIconTreeTypeEntity> {
    headerId?: string;
    name?: string;
    grouping?: string;
    orderNum?: number;
    remark?: string;
    iconTreeType?: string;
}