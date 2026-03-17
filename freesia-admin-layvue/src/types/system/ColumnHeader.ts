import {BaseEntity, BaseVo} from "@/types/common";
import {SysColumnDetailEntity} from "@/types/system/ColumnDetail";

export interface SysColumnHeaderVo extends BaseVo {
    componentId?: string;
    name?: string;
    height?: number;
    maxHeight?: number;
    initPageSize?: number;
    enabled?: boolean;
    resizeFlag?: boolean;
    autoColsWidthFlag?: boolean;
    defaultToolBarFlag?: boolean;
    component?: string;
}

export interface SysColumnHeaderEntity extends BaseEntity {
    componentId?: string;
    name?: string;
    height?: number;
    maxHeight?: number;
    initPageSize?: number;
    enabled?: boolean;
    resizeFlag?: boolean;
    autoColsWidthFlag?: boolean;
    defaultToolBarFlag?: boolean;
    component?: string;
    sysColumnDetailList?: Array<SysColumnDetailEntity>;
}