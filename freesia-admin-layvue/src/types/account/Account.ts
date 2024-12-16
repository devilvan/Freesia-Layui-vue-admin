import {BaseEntity, BaseVo} from "../Common";

export interface AccountCostVo extends BaseVo {
    desc?: string;
    amount?: string;
    type?: string;
    paymentSign?: string;
    paymentTime?: Date;
    icon?: string;
    remark?: string;
}

export interface AccountCostEntity extends BaseEntity {
    desc?: string;
    amount?: string;
    type?: string;
    paymentSign?: string;
    paymentTime?: Date;
    icon?: string;
    remark?: string;
}