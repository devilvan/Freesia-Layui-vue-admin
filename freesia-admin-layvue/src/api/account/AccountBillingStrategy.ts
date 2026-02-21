import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {AccountBillingStrategyEntity, AccountBillingStrategyVo} from "@/types/account/AccountBillingStrategy";

export function saveUpdate(accountBillingStrategyVo: AccountBillingStrategyVo): Promise<R<void>> {
 return Http.post("/api/accountBillingStrategyController/saveUpdate", accountBillingStrategyVo);
 }

export function saveUpdateBatch(accountBillingStrategyVoList: Array<AccountBillingStrategyVo>): Promise<R<void>> {
 return Http.post("/api/accountBillingStrategyController/saveUpdateBatch", accountBillingStrategyVoList);
}

export function findPageAccountBillingStrategy(accountBillingStrategyVo: AccountBillingStrategyVo, pageQuery: PageQuery): Promise<TableResult<AccountBillingStrategyEntity>> {
 let params = buildPageUrlParam(accountBillingStrategyVo, pageQuery);
 return Http.get("/api/accountBillingStrategyController/findPageAccountBillingStrategy", params);
}

export function findAccountBillingStrategy(accountBillingStrategyVo: AccountBillingStrategyVo): Promise<R<AccountBillingStrategyEntity>> {
 let params = buildUrlParam(accountBillingStrategyVo);
 return Http.get("/api/accountBillingStrategyController/findAccountBillingStrategy", params);
}

export function findListAccountBillingStrategy(accountBillingStrategyVo: AccountBillingStrategyVo): Promise<R<AccountBillingStrategyEntity>> {
 let params = buildUrlParam(accountBillingStrategyVo);
 return Http.get("/api/accountBillingStrategyController/findListAccountBillingStrategy", params);
}

export function deleteAccountBillingStrategy(idList: Array<string>): Promise<R<void>> {
 return Http.post("/api/accountBillingStrategyController/deleteAccountBillingStrategy", idList);
}