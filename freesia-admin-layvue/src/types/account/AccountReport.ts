import {BaseEntity, BaseVo} from "@/types/common";

export interface AccountReportVo extends BaseVo {
    remark?: string;
    userId?: number;
    budgetId?: number;
    strategyId?: number;
    title?: string;
    budgetType?: string;
    outlay?: number;
    incomeAmount?: number;
    billingTime?: string;
    billingTimeFrom?: string;
    billingTimeTo?: string;
    recalculateFlag?: boolean;
}

export interface AccountReportEntity extends BaseEntity {
    remark?: string;
    userId?: number;
    budgetId?: number;
    strategyId?: number;
    title?: string;
    budgetType?: string;
    outlay?: number;
    incomeAmount?: number;
    billingTime?: string;
    billingTimeFrom?: string;
    billingTimeTo?: string;
    recalculateFlag?: boolean;
}