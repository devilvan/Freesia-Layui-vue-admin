import Http from "../Http";
import {LaySelectEntity, PageQuery} from "../../types/Common";
import {R, TableResult} from "../../types/Result";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {
    AccountCostEntity,
    AccountCostVo, EchartStackedHorizontalBarOptionEntity, FindCacheCostTypeVo,
    FindCostLineChartVo,
    FindCostSumCalendarNearYearVo, FindRankByCostTypeVo
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

export function findRankByCostType(findRankByCostTypeVo: FindRankByCostTypeVo): Promise<R<EchartStackedHorizontalBarOptionEntity>> {
    let params = buildUrlParam(findRankByCostTypeVo);
    return Http.get("/api/accountCostController/findRankByCostType", params);
}

export function refreshCache(): Promise<void> {
    return Http.post("/api/accountCostController/refreshCache");
}

export function findSelectCostTypeList(accountCostVo: AccountCostVo): Promise<R<LaySelectEntity[]>> {
    let params = buildUrlParam(accountCostVo);
    return Http.get("/api/accountCostController/findSelectCostTypeList", params);
}

export function findListSelectCostType(): Promise<R<LaySelectEntity[]>> {
    return Http.get("/api/accountCostController/findListSelectCostType");
}

export function findCacheCostType(vo: FindCacheCostTypeVo): Promise<R<LaySelectEntity[]>> {
    let params = buildUrlParam(vo)
    return Http.get("/api/accountCostController/findCacheCostType", params);
}

