import {BaseEntity, BaseVo} from "@/types/common";

export interface SysColumnMiddleVo extends BaseVo {
    headerId?: number;
    title?: string;
    name?: string;
    enabled?: boolean;
}

export interface SysColumnMiddleEntity extends BaseEntity {
    headerId?: number;
    title?: string;
    name?: string;
    enabled?: boolean;
}