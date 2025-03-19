import Http from "../Http";
import {PageQuery} from "../../types/Common";
import {TableResult} from "../../types/Result";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {AccountBudgetEntity, AccountBudgetVo, EchartCapacityOptionEntity} from "../../types/account/AccountBudget";

export function saveUpdate(accountBudgetVo: AccountBudgetVo) {
    return Http.post("/api/accountBudgetController/saveUpdate", accountBudgetVo);
}

export function saveUpdateBatch(accountBudgetVoList: Array<AccountBudgetVo>) {
    return Http.post("/api/accountBudgetController/saveUpdateBatch", accountBudgetVoList);
}

export function findPageAccountBudget(accountBudgetVo: AccountBudgetVo, pageQuery: PageQuery): Promise<TableResult<AccountBudgetEntity>> {
    let params = buildPageUrlParam(accountBudgetVo, pageQuery);
    return Http.get("/api/accountBudgetController/findPageAccountBudget", params);
}

export function findAccountBudget(accountBudgetVo: AccountBudgetVo): Promise<AccountBudgetEntity> {
    let params = buildUrlParam(accountBudgetVo);
    return Http.get("/api/accountBudgetController/findAccountBudget", params);
}

export function deleteAccountBudget(idList: Array<string>) {
    return Http.post("/api/accountBudgetController/deleteAccountBudget", idList);
}

export function findBudgetCapacity(accountBudgetVo: AccountBudgetVo): Promise<Array<EchartCapacityOptionEntity>> {
    let params = buildUrlParam(accountBudgetVo);
    return Http.get("/api/accountBudgetController/findBudgetCapacity", params);
}