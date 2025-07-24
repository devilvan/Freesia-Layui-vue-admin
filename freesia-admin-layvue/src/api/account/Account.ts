import Http from "../Http";
import {PageQuery} from "../../types/Common";
import {R, TableResult} from "../../types/Result";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {
    AccountCostEntity,
    AccountCostVo,
    FindCostLineChartVo,
    FindCostSumCalendarNearYearVo
} from "../../types/account/Account";
import {AccountBudgetVo} from "../../types/account/AccountBudget";

export function saveUpdate(accountCostVo: AccountCostVo) {
    return Http.post("/api/accountCostController/saveUpdate", accountCostVo);
}

export function saveUpdateBatch(accountCostVoList: Array<AccountCostVo>) {
    return Http.post("/api/accountCostController/saveUpdateBatch", accountCostVoList);
}

export function findPageAccountCost(accountCostVo: AccountCostVo, pageQuery: PageQuery): Promise<TableResult<AccountCostEntity>> {
    let params = buildPageUrlParam(accountCostVo, pageQuery);
    return Http.get("/api/accountCostController/findPageAccountCost", params);
}

export function findAccountCost(accountCostVo: AccountCostVo): Promise<AccountCostEntity> {
    let params = buildUrlParam(accountCostVo);
    return Http.get("/api/accountCostController/findAccountCost", params);
}

export function deleteAccountCost(idList: Array<string>) {
    return Http.post("/api/accountCostController/deleteAccountCost", idList);
}

export const accountsImport = function (file: File) {
    let params = {
        file: file,
    }
    return Http.post('/api/accountCostController/accountsImport', params, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

export function accountsExport(accountsExportVo: AccountCostVo) {
    let params = buildUrlParam(accountsExportVo);
    return Http.get("/api/accountCostController/accountsExport", params, {
        responseType: 'blob'
    });
}

export function findCostTypeRatePie(accountCostVo: AccountCostVo) {
    let params = buildUrlParam(accountCostVo);
    return Http.get("/api/accountCostController/findCostTypeRatePie", params);
}

export function findCostLineChart(findCostLineChartVo: FindCostLineChartVo) {
    let params = buildUrlParam(findCostLineChartVo);
    return Http.get("/api/accountCostController/findCostLineChart", params);
}

export function findCostSumCalendarNearYear(findCostSumCalendarNearYearVo: FindCostSumCalendarNearYearVo) {
    let params = buildUrlParam(findCostSumCalendarNearYearVo);
    return Http.get("/api/accountCostController/findCostSumCalendarNearYear", params);
}

export function findRankByCostType(accountCostVo: AccountCostVo) {
    let params = buildUrlParam(accountCostVo);
    return Http.get("/api/accountCostController/findRankByCostType", params);
}