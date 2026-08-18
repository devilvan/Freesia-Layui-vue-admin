import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {AccountReportEntity, AccountReportVo} from "@/types/account/AccountReport";

export function saveUpdate(accountReportVo: AccountReportVo): Promise<R<void>> {
 return Http.post("/api/accountReportController/saveUpdate", accountReportVo);
 }

export function saveUpdateBatch(accountReportVoList: Array<AccountReportVo>): Promise<R<void>> {
 return Http.post("/api/accountReportController/saveUpdateBatch", accountReportVoList);
}

export function findPageAccountReport(accountReportVo: AccountReportVo, pageQuery: PageQuery): Promise<TableResult<AccountReportEntity>> {
 let params = buildPageUrlParam(accountReportVo, pageQuery);
 return Http.get("/api/accountReportController/findPageAccountReport", params);
}

export function findAccountReport(accountReportVo: AccountReportVo): Promise<R<AccountReportEntity>> {
 let params = buildUrlParam(accountReportVo);
 return Http.get("/api/accountReportController/findAccountReport", params);
}

export function findListAccountReport(accountReportVo: AccountReportVo): Promise<R<AccountReportEntity>> {
 let params = buildUrlParam(accountReportVo);
 return Http.get("/api/accountReportController/findListAccountReport", params);
}

export function deleteAccountReport(idList: Array<string>): Promise<R<void>> {
 return Http.post("/api/accountReportController/deleteAccountReport", idList);
}

export function updateBudgetAmount(accountReportVo: AccountReportVo): Promise<R<void>> {
 return Http.post("/api/accountReportController/updateBudgetAmount", accountReportVo);
}

export function recalculateReport(idList: Array<string>): Promise<R<void>> {
 return Http.post("/api/accountReportController/recalculateReportByIdList", idList);
}