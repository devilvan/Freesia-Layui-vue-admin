import {BaseEntity, BaseVo} from "../Common";
import {SysUserEntity} from "../system/User";
import {AccountCostUserAllocVo} from "@/types/account/AccountCostUserAlloc";

export interface AccountCostVo extends BaseVo {
    costDesc?: string;
    outlay?: number;
    costType?: string;
    paymentSign?: string;
    paymentTime?: Date;
    paymentTimeRange?: string[];
    icon?: string;
    remark?: string;
    accountCostUserIdList?: string[];
    accountCostUserNameList?: string[];
    acNickName?: string;
    userList?: SysUserEntity[];
    status?: boolean;
    iconName?: string;
    iconPartition?: string;
    allTenantFlag?: boolean;
    costTypeList?: string[];
    refUserIdList?: string[];
    accountCostUserAllocVoList?: AccountCostUserAllocVo[]
}

export interface AccountCostEntity extends BaseEntity {
    costDesc?: string;
    outlay?: number;
    type?: string;
    paymentSign?: string;
    paymentTime?: Date;
    icon?: string;
    remark?: string;
    accountCostUserId?: string
    accountCostUserName?: string
    accountCostUserAllocVoList?: AccountCostUserAllocVo[]
    allocAmount?: number
    allocStatus?: string
    tenantName?: string
}

export interface FindCacheCostTypeVo extends BaseVo {
    costDesc?: string
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

export interface FindRankByCostTypeVo extends AccountCostVo {
    dateScope?: DateScope
}

export interface EchartStackedHorizontalBarOptionEntity {
    /**
     * Y轴键
     */
    yAxis?: string[],
    /**
     * X轴值
     */
    series?: Series[]

}

export interface FindCacheCostTypeEntity {
    value?: string,
    iconUrl?: string,
    disabled?: boolean
}

/**
 * Y轴值
 */
interface Series {
    /**
     * 名称
     */
    name?: string,
    /**
     * 值
     */
    value?: number[],
    /**
     * 栈
     */
    stack?: string,
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