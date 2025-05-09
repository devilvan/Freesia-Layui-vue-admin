import {saveUpdate} from "@/api/common/icon/Icon";
import {SysOssEntity} from "@/types/system/Oss";
import {BaseEntity, BaseVo} from "@/types/Common";
import {Tree} from "@/types/Result";
import {FindCommonIconEntity} from "@/types/common/icon/Icon";

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
    iconId?: string;
    parentId?: string;
    originName?: string;
    url?: string;
    multipleIconList?: FindCommonIconEntity[]
    idList?: string[];
}

export interface CommonIconTemplateDetailEntity extends BaseEntity {
    headerId?: string;
    name?: string;
    grouping?: string;
    orderNum?: number;
    remark?: string;
    iconTreeType?: string;
    iconId?: string;
    parentId?: string;
    url?: string
}

export interface FindTreeIconTreeTypeEntity extends Tree<FindTreeIconTreeTypeEntity> {
    headerId?: string;
    name?: string;
    grouping?: string;
    orderNum?: number;
    remark?: string;
    iconTreeType?: string;
    url?: string
}

export interface FindMaxOrderNumVo extends BaseVo {
    headerId?: string;
    name?: string;
    grouping?: string;
    iconTreeType?: string;
    parentId?: string;
}