import {BaseEntity, BaseVo} from "@/types/common";

export interface AccountBillingStrategyVo extends BaseVo {
    remark?: string;
    userId?: number;
    budgetId?: number;
    budgetType?: string;
    generateTime?: string;
    generateFlag?: string;
    enabled?: string;
    weekBegin?: number;
}

export interface AccountBillingStrategyEntity extends BaseEntity {
    remark?: string;
    userId?: number;
    budgetId?: number;
    budgetType?: string;
    generateTime?: string;
    generateFlag?: string;
    enabled?: string;
    weekBegin?: number;
}