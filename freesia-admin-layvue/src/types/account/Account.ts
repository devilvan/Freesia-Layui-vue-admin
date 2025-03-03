import {BaseEntity, BaseVo} from "../Common";
import {SysUserEntity} from "../system/User";

export interface AccountCostVo extends BaseVo {
    costDesc?: string;
    outlay?: string;
    costType?: string;
    paymentSign?: string;
    paymentTime?: Date;
    paymentTimeRange?: string[];
    icon?: string;
    remark?: string;
    accountCostUserIdList?: string[];
    accountCostUserNameList?: string[];
    acNickName?: string;
    userList: SysUserEntity[];
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

export interface FindCostLineChartVo extends AccountCostVo {
    dateScope?: string;
    dateValue?: string;
}

export interface FindCostSumCalendarNearYearVo extends AccountCostVo {
}

export enum DateScope {
    /**
     * 周
     */
    WEEK = "WEEK",
    /**
     * 月
     */
    MONTH = "MONTH",
    /**
     * 年
     */
    YEAR = "YEAR",

}