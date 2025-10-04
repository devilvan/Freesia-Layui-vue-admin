import {BaseEntity, BaseVo} from "../Common";

export interface AccountCostUserAllocVo extends BaseVo {
    accountId?: string;
    userId?: string;
    amount?: number;
    operateTime?: Date;
    allocFlag?: boolean;
}

export interface AccountCostUserAllocEntity extends BaseEntity {
    accountId?: string;
    userId?: string;
    amount?: number;
    operateTime?: Date;
    allocFlag?: boolean;
}
