import {BaseEntity, BaseVo} from "@/types/common";

export interface AccountBillingStrategyVo extends BaseVo {
    remark?: string;
    userId?: number;
    budgetId?: number;
    budgetType?: string;
    generateTime?: string;
    nextGenerateTime?: string;
    generateFlag?: boolean;
    enabled?: boolean;
    weekBegin?: number;
    recalculateFlag?: boolean;
}

export interface AccountBillingStrategyEntity extends BaseEntity {
    remark?: string;
    userId?: number;
    budgetId?: number;
    budgetType?: string;
    generateTime?: string;
    generateFlag?: boolean;
    enabled?: boolean;
    weekBegin?: number;
    recalculateFlag?: boolean;
}