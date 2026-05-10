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

export interface RpFindAllocAmountDto {
    collected?: Array<Alloc>;
    totalCollected?: number;
    allocated?: Array<Alloc>;
    totalAllocated?: number;
}

export interface Alloc {
    id?: number;
    userId?: string;
    nickName?: string;
    amount?: number;
    operateTime?: Date;
    costDesc?: string;
    remark?: string;
}
