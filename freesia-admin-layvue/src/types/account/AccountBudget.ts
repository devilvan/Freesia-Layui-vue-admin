import {BaseEntity, BaseVo} from "../Common";

export interface AccountBudgetVo extends BaseVo {
    budgetDesc?: string;
    outlay?: number;
    durationFrom?: Date;
    durationTo?: Date;
    budgetType?: string;
    remark?: string;
}

export interface AccountBudgetEntity extends BaseEntity {
    budgetDesc?: string;
    outlay?: number;
    durationFrom?: Date;
    durationTo?: Date;
    budgetType?: string;
    remark?: string;
}