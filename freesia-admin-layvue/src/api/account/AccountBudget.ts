import Http from "../Http";
import {PageQuery} from "../../types/Common";
import {TableResult} from "../../types/Result";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {AccountBudgetEntity, AccountBudgetVo} from "../../types/account/AccountBudget";

export function saveUpdate(sysTenantVo: AccountBudgetVo) {
    return Http.post("/api/accountBudgetController/saveUpdate", sysTenantVo);
}

export function saveUpdateBatch(sysTenantVoList: Array<AccountBudgetVo>) {
    return Http.post("/api/accountBudgetController/saveUpdateBatch", sysTenantVoList);
}

export function findPageAccountBudget(sysTenantVo: AccountBudgetVo, pageQuery: PageQuery): Promise<TableResult<AccountBudgetEntity>> {
    let params = buildPageUrlParam(sysTenantVo, pageQuery);
    return Http.get("/api/accountBudgetController/findPageAccountBudget", params);
}

export function findAccountBudget(sysTenantVo: AccountBudgetVo): Promise<AccountBudgetEntity> {
    let params = buildUrlParam(sysTenantVo);
    return Http.get("/api/accountBudgetController/findAccountBudget", params);
}

export function deleteAccountBudget(idList: Array<string>) {
    return Http.post("/api/accountBudgetController/deleteAccountBudget", idList);
}