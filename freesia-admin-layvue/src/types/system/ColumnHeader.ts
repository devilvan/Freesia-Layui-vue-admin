import {BaseEntity, BaseVo} from "@/types/common";

export interface SysColumnHeaderVo extends BaseVo {
    component?: string;
    enabled?: boolean;
}

export interface SysColumnHeaderEntity extends BaseEntity {
    component?: string;
    enabled?: boolean;
}