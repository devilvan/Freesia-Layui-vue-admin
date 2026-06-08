import {BaseEntity, BaseVo} from "../Common";

export interface AccountBudgetVo extends BaseVo {
    budgetDesc?: string;
    outlay?: number;
    durationFrom?: Date;
    durationTo?: Date;
    budgetType?: string;
    remark?: string;
    allTenantFlag?: boolean
}

export interface AccountBudgetEntity extends BaseEntity {
    budgetDesc?: string;
    outlay?: number;
    durationFrom?: Date;
    durationTo?: Date;
    budgetType?: string;
    remark?: string;
}

export interface EchartCapacityOptionEntity {
    id?: string;
    name?: string;
    value?: number;
    outlay?: number;
    budget?: number;
    durationFrom?: string;
    durationTo?: string;
    tenantName?: string;
}
