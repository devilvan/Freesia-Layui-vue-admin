import {BaseEntity, BaseVo} from "../Common";

export interface SysSensitiveLogVo extends BaseVo {
    operatorName?: string;
    deptName?: string;
    operateTimeFrom?: Date;
    operateTimeTo?: Date;
    module?: string;
    subModule?: string;
    type?: string;
    result?: string;
    sign?: string;
}

export interface SysSensitiveEntity extends BaseEntity {
    operatorId?: number;
    operatorName?: string;
    deptId?: number;
    deptName?: string;
    methodType?: string;
    url?: string;
    beOperatedId?: number;
    beOperatedName?: string;
    ipAddress?: string;
    location?: string;
    operateTime?: Date;
    browser?: string;
    os?: string;
    module?: string;
    subModule?: string;
    type?: string;
    result?: string;
    contextOld?: string;
    context?: string;
    sign?: string;
    remark?: string;
}
