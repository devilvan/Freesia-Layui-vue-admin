import {saveUpdate} from "@/api/common/icon/Icon";
import {SysOssEntity} from "@/types/system/Oss";
import {BaseEntity, BaseVo} from "@/types/Common";

export interface CommonTodoVo extends BaseVo {
    userId?: string;
    title?: string;
    desc?: string;
    content?: string;
    status?: string;
    dueTime?: Date;
    reminderSendFlag?: boolean;
    priority?: number;
    remark?: string;
}

export interface CommonTodoEntity extends BaseEntity {
    userId?: string;
    title?: string;
    desc?: string;
    content?: string;
    status?: string;
    dueTime?: Date;
    reminderSendFlag?: boolean;
    priority?: number;
    remark?: string;
}