/**
 * 查询参数
 */
import {BaseEntity, BaseVo} from "../Common";
import {Tree} from "../Result";

export enum MenuType {
    DIR = "D",
    MENU = "M",
    BUTTON = "B",
    LINK = "L"
}

export interface SysDictVo extends BaseVo {
    keyId?: string;
    keyName?: string;
    dictKey?: string;
    valueName?: string;
    value?: string;
    status?: string;
    keyNameOrDictKey?: string
}

export interface SysDictKeyVo extends BaseVo {
    id?: string
    dictKey?: string
    keyName?: string
    remark?: string
}

export interface SysDictKeyEntity extends BaseEntity {
    keyName?: string;
    dictKey?: string;
    status?: string;
    remark?: string;
}

export interface SysDictValueEntity extends BaseEntity {
    keyId?: number;
    dictKey?: string;
    valueName?: string;
    value?: string;
    orderNum?: number;
    isDefault?: string;
    i18n?: string;
    cssStyle?: string;
    status?: string;
    remark?: string;
}

export interface SysDictValueVo extends BaseVo {
    keyId?: string;
    dictKey?: string;
    valueName?: string;
    value?: string;
    orderNum?: number;
    isDefault?: string;
    status?: string;
    remark?: string;
    cssStyle?: string;
}

export interface SysDictValueModel {
    label: string,
    value: string
}

export interface MatchDictValueModel {
    valueName?: string,
    cssStyle?: string
}

export interface FindTreeMenuSelectEntity extends Tree<FindTreeMenuSelectEntity> {
    title?: string;
    menuType?: string;
    field?: string;
}
