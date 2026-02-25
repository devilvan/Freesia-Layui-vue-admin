import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {AccountReportStrategyEntity, AccountReportStrategyVo} from "@/types/account/AccountReportStrategy";

export function saveUpdate(accountReportStrategyVo: AccountReportStrategyVo): Promise<R<void>> {
 return Http.post("/api/accountReportStrategyController/saveUpdate", accountReportStrategyVo);
 }

export function saveUpdateBatch(accountReportStrategyVoList: Array<AccountReportStrategyVo>): Promise<R<void>> {
 return Http.post("/api/accountReportStrategyController/saveUpdateBatch", accountReportStrategyVoList);
}

export function findPageAccountReportStrategy(accountReportStrategyVo: AccountReportStrategyVo, pageQuery: PageQuery): Promise<TableResult<AccountReportStrategyEntity>> {
 let params = buildPageUrlParam(accountReportStrategyVo, pageQuery);
 return Http.get("/api/accountReportStrategyController/findPageAccountReportStrategy", params);
}

export function findAccountReportStrategy(accountReportStrategyVo: AccountReportStrategyVo): Promise<R<AccountReportStrategyEntity>> {
 let params = buildUrlParam(accountReportStrategyVo);
 return Http.get("/api/accountReportStrategyController/findAccountReportStrategy", params);
}

export function findListAccountReportStrategy(accountReportStrategyVo: AccountReportStrategyVo): Promise<R<AccountReportStrategyEntity>> {
 let params = buildUrlParam(accountReportStrategyVo);
 return Http.get("/api/accountReportStrategyController/findListAccountReportStrategy", params);
}

export function deleteAccountReportStrategy(idList: Array<string>): Promise<R<void>> {
 return Http.post("/api/accountReportStrategyController/deleteAccountReportStrategy", idList);
}