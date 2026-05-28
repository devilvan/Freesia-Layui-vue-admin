import Http from "../Http";
import {PageQuery} from "../../types/Common";
import {R, TableResult} from "../../types/Result";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {AccountReportEntity, AccountReportVo} from "../../types/account/AccountReport";

export function saveUpdate(accountReportVo: AccountReportVo): Promise<R<void>> {
    return Http.post("/api/accountReportController/saveUpdate", accountReportVo);
}

export function findPageAccountReport(accountReportVo: AccountReportVo, pageQuery: PageQuery): Promise<TableResult<AccountReportEntity>> {
    let params = buildPageUrlParam(accountReportVo, pageQuery);
    return Http.get("/api/accountReportController/findPageAccountReport", params);
}

export function findAccountReport(accountReportVo: AccountReportVo): Promise<R<AccountReportEntity>> {
    let params = buildUrlParam(accountReportVo);
    return Http.get("/api/accountReportController/findAccountReport", params);
}

export function updateBudgetAmount(accountReportVo: AccountReportVo): Promise<R<void>> {
    return Http.post("/api/accountReportController/updateBudgetAmount", accountReportVo);
}
