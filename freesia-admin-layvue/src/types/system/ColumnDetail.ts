import {BaseEntity, BaseVo} from "@/types/common";

export interface SysColumnDetailVo extends BaseVo {
    userId?: number;
    headerId?: number;
    title?: string;
    enabled?: boolean;
    fixed?: boolean;
    ellipsisTooltip?: boolean;
    width?: string;
    minWidth?: string;
    maxWidth?: string;
    orderNum?: number;
    sorted?: string;
}

export interface SysColumnDetailEntity extends BaseEntity {
    userId?: number;
    headerId?: number;
    title?: string;
    enabled?: boolean;
    fixed?: boolean;
    ellipsisTooltip?: boolean;
    width?: string;
    minWidth?: string;
    maxWidth?: string;
    orderNum?: number;
    sorted?: string;
}