import Http from "../Http";
import { PageQuery } from "../../types/Common";
import { R, TableResult } from "../../types/Result";
import { buildPageUrlParam, buildUrlParam } from "../../util/URequest";
import {
    AccountCostEntity,
    AccountCostVo,
    AccountCostMoveVo,
    FindCacheCostTypeEntity,
    FindCacheCostTypeVo
} from "../../types/account/Account";

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

export function findSelectCostTypeList(accountCostVo: AccountCostVo): Promise<R<Array<any>>> {
    let params = buildUrlParam(accountCostVo);
    return Http.get("/api/accountCostController/findSelectCostTypeList", params);
}

export function findListSelectCostType(): Promise<R<Array<any>>> {
    return Http.get("/api/accountCostController/findListSelectCostType");
}

export function findCacheCostType(vo: FindCacheCostTypeVo): Promise<R<FindCacheCostTypeEntity[]>> {
    let params = buildUrlParam(vo)
    return Http.get("/api/accountCostController/findCacheCostType", params);
}

export function findCostTypeRatePie(accountCostVo: AccountCostVo) {
    let params = buildUrlParam(accountCostVo);
    return Http.get("/api/accountCostController/findCostTypeRatePie", params);
}

export function findCostLineChart(findCostLineChartVo: any) {
    let params = buildUrlParam(findCostLineChartVo);
    return Http.get("/api/accountCostController/findCostLineChart", params);
}

export function findCostSumCalendarNearYear(findCostSumCalendarNearYearVo: any) {
    let params = buildUrlParam(findCostSumCalendarNearYearVo);
    return Http.get("/api/accountCostController/findCostSumCalendarNearYear", params);
}

export function findRankByCostType(findRankByCostTypeVo: any) {
    let params = buildUrlParam(findRankByCostTypeVo);
    return Http.get("/api/accountCostController/findRankByCostType", params);
}

export function refreshCache(): Promise<any> {
    return Http.post("/api/accountCostController/refreshCache");
}

export function findListSysUserById(idList: string[]): Promise<R<any[]>> {
    const params = { idList: idList };
    return Http.get('/api/accountCostUserAllocController/findListSysUserById', params);
}

export function findListAllocByCostId(costId: string): Promise<R<any[]>> {
    const params = { costId: costId };
    return Http.get('/api/accountCostUserAllocController/findListAllocByCostId', params);
}

export function moveTenant(accountCostMoveVo: AccountCostMoveVo) {
    return Http.post("/api/accountCostController/moveTenant", accountCostMoveVo);
}