import Http from "../Http";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {PageQuery} from "../../types/Common";
import {R} from "../../types/Result";
import {AccountBudgetVo, AccountBudgetEntity, EchartCapacityOptionEntity} from "../../types/account/AccountBudget";

export function saveUpdate(accountBudgetVo: AccountBudgetVo): Promise<R<void>> {
    return Http.post("/api/accountBudgetController/saveUpdate", accountBudgetVo);
}

export function saveUpdateBatch(accountBudgetVoList: Array<AccountBudgetVo>): Promise<R<void>> {
    return Http.post("/api/accountBudgetController/saveUpdateBatch", accountBudgetVoList);
}

export function findPageAccountBudget(accountBudgetVo: AccountBudgetVo, pageQuery: PageQuery) {
    let params = buildPageUrlParam(accountBudgetVo, pageQuery);
    return Http.get("/api/accountBudgetController/findPageAccountBudget", params);
}

export function findAccountBudget(accountBudgetVo: AccountBudgetVo): Promise<R<AccountBudgetEntity>> {
    let params = buildUrlParam(accountBudgetVo);
    return Http.get("/api/accountBudgetController/findAccountBudget", params);
}

export function deleteAccountBudget(idList: Array<string>): Promise<R<void>> {
    return Http.post("/api/accountBudgetController/deleteAccountBudget", idList);
}

export function findBudgetCapacity(accountBudgetVo: AccountBudgetVo): Promise<R<Array<EchartCapacityOptionEntity>>> {
    let params = buildUrlParam(accountBudgetVo);
    return Http.get("/api/accountBudgetController/findBudgetCapacity", params);
}
