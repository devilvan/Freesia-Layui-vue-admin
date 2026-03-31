import {BaseEntity, BaseVo} from "@/types/common";
import {SysColumnDetailEntity} from "@/types/system/ColumnDetail";

export interface SysColumnHeaderVo extends BaseVo {
    componentId?: string;
    name?: string;
    description?: string;
    height?: number;
    maxHeight?: number;
    initPageSize?: number;
    enabled?: boolean;
    resizeFlag?: boolean;
    autoColsWidthFlag?: boolean;
    defaultToolBarFlag?: boolean;
    defaultColumnList?: Array<DefaultColumnVo>;
}

export interface SysColumnHeaderEntity extends BaseEntity {
    componentId?: string;
    name?: string;
    description?: string;
    height?: number;
    maxHeight?: number;
    initPageSize?: number;
    enabled?: boolean;
    resizeFlag?: boolean;
    autoColsWidthFlag?: boolean;
    defaultToolBarFlag?: boolean;
    sysColumnDetailList?: Array<SysColumnDetailEntity>;
}

export interface DefaultColumnVo {
    hide: boolean;
    sorted: string | null;
    width: number;
    resizeFlag: boolean | string;
    minWidth: number;
    fixed: "left" | "right" | null;
    title: string;
    ellipsisTooltip: boolean;
    key?: string
}