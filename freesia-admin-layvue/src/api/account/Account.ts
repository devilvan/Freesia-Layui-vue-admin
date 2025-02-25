import Http from "../Http";
import {PageQuery} from "../../types/Common";
import {TableResult} from "../../types/Result";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {
    AccountCostEntity,
    AccountCostVo,
    FindCostSumCalendarNearYearVo,
    FindCostLineChartVo
} from "../../types/account/Account";
import {layer} from "@layui/layui-vue";

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