import {BaseEntity, BaseVo} from "@/types/common";

export interface SysColumnDetailVo extends BaseVo {
    userId?: number;
    headerId?: number;
    title?: string;
    name?: string;
    enabled?: boolean;
    fixed?: boolean;
    ellipsisTooltip?: boolean;
    width?: number;
    minWidth?: number;
    maxWidth?: number;
    orderNum?: number;
    sorted?: string;
}

export interface SysColumnDetailEntity extends BaseEntity {
    userId?: number;
    headerId?: number;
    title?: string;
    name?: string;
    enabled?: boolean;
    fixed?: boolean;
    ellipsisTooltip?: boolean;
    width?: number;
    minWidth?: number;
    maxWidth?: number;
    orderNum?: number;
    sorted?: string;
}