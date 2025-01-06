import {BaseEntity, BaseVo} from "../Common";

export interface AccountCostVo extends BaseVo {
    costDesc?: string;
    outlay?: string;
    costType?: string;
    paymentSign?: string;
    paymentTime?: Date;
    paymentTimeRange?: string[];
    icon?: string;
    remark?: string;
}

export interface AccountCostEntity extends BaseEntity {
    costDesc?: string;
    outlay?: string;
    type?: string;
    paymentSign?: string;
    paymentTime?: Date;
    icon?: string;
    remark?: string;
}

export enum PaymentSign {
    /**
     * 支出
     */
    EXPENSES = "EXPENSES",
    /**
     * 收入
     */
    INCOME = "INCOME",

}