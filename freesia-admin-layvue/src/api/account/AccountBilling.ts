import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {AccountBillingEntity, AccountBillingVo} from "@/types/account/AccountBilling";

export function saveUpdate(accountBillingVo: AccountBillingVo): Promise<R<void>> {
 return Http.post("/api/accountBillingController/saveUpdate", accountBillingVo);
 }

export function saveUpdateBatch(accountBillingVoList: Array<AccountBillingVo>): Promise<R<void>> {
 return Http.post("/api/accountBillingController/saveUpdateBatch", accountBillingVoList);
}

export function findPageAccountBilling(accountBillingVo: AccountBillingVo, pageQuery: PageQuery): Promise<TableResult<AccountBillingEntity>> {
 let params = buildPageUrlParam(accountBillingVo, pageQuery);
 return Http.get("/api/accountBillingController/findPageAccountBilling", params);
}

export function findAccountBilling(accountBillingVo: AccountBillingVo): Promise<R<AccountBillingEntity>> {
 let params = buildUrlParam(accountBillingVo);
 return Http.get("/api/accountBillingController/findAccountBilling", params);
}

export function findListAccountBilling(accountBillingVo: AccountBillingVo): Promise<R<AccountBillingEntity>> {
 let params = buildUrlParam(accountBillingVo);
 return Http.get("/api/accountBillingController/findListAccountBilling", params);
}

export function deleteAccountBilling(idList: Array<string>): Promise<R<void>> {
 return Http.post("/api/accountBillingController/deleteAccountBilling", idList);
}