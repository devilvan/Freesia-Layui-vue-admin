import Http from "../Http";
import {PageQuery} from "../../types/Common";
import {TableResult} from "../../types/Result";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {AccountCostEntity, AccountCostVo} from "../../types/account/Account";

export function saveUpdate(sysTenantVo: AccountCostVo) {
    return Http.post("/api/accountCostController/saveUpdate", sysTenantVo);
}

export function saveUpdateBatch(sysTenantVoList: Array<AccountCostVo>) {
    return Http.post("/api/accountCostController/saveUpdateBatch", sysTenantVoList);
}

export function findPageAccountCost(sysTenantVo: AccountCostVo, pageQuery: PageQuery): Promise<TableResult<AccountCostEntity>> {
    let params = buildPageUrlParam(sysTenantVo, pageQuery);
    return Http.get("/api/accountCostController/findPageAccountCost", params);
}

export function findAccountCost(sysTenantVo: AccountCostVo): Promise<AccountCostEntity> {
    let params = buildUrlParam(sysTenantVo);
    return Http.get("/api/accountCostController/findAccountCost", params);
}

export function deleteAccountCost(idList: Array<string>) {
    return Http.post("/api/accountCostController/deleteAccountCost", idList);
}
