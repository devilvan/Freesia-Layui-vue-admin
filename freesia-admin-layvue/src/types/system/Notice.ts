import {BaseEntity, BaseVo} from "@/types/Common";

export enum SysNoticeType {
    ANNOUNCEMENT = 'ANNOUNCEMENT',
    NOTICE = 'NOTICE'
}

export interface SysNoticeVo extends BaseVo {
    title?: string;
    type?: string;
    effectiveTimeFrom?: string;
    effectiveTimeTo?: string;
    content?: string;
    publisherId?: string;
    remark?: string;
}

export interface SysNoticeEntity extends BaseEntity {
    title?: string;
    type?: string;
    effectiveTimeFrom?: Date;
    effectiveTimeTo?: Date;
    content?: string;
    publisherId?: string;
    remark?: string;
}

export interface MarkReadVo {
    idList: string[],
    type?: SysNoticeType
}