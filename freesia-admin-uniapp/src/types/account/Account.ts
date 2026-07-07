import { BaseEntity, BaseVo } from "../Common";
import { SysUserEntity } from "../system/User";
import { AccountCostUserAllocVo } from "./AccountCostUserAlloc";

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
    EXPENSES = "EXPENSES",
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
    yAxis?: string[],
    series?: Series[]
}

export interface FindCostLineChartVo extends AccountCostVo {
    dateScope?: string;
    dateValue?: string;
}

export interface FindCostSumCalendarNearYearVo extends AccountCostVo {
}

export interface FindCacheCostTypeEntity {
    value?: string,
    iconUrl?: string,
    disabled?: boolean
}

interface Series {
    name?: string,
    value?: number[],
    stack?: string,
}

export enum DateScope {
    WEEK = "WEEK",
    MONTH = "MONTH",
    YEAR = "YEAR",
}

export interface CostTypeRatePieEntity {
    name?: string;
    value?: number;
    icon?: string;
}

export interface CostLineChartEntity {
    xAxis?: string[];
    series?: Series[];
}

export interface CostCalendarEntity {
    date?: string;
    value?: number;
}

export interface AccountCostMoveVo {
    idList: Array<string>;
    targetTenantId: string;
}