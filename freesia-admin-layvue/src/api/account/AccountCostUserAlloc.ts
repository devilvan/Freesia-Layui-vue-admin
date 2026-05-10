import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {
    AccountCostUserAllocEntity,
    AccountCostUserAllocVo,
    RpFindAllocAmountDto
} from "@/types/account/AccountCostUserAlloc";
import {FindPageSysUserListEntity} from "@/types/system/User";

export function saveUpdate(accountCostUserAllocVo: AccountCostUserAllocVo) {
    return Http.post("/api/accountCostUserAllocController/saveUpdate", accountCostUserAllocVo);
}

export function saveUpdateBatch(accountCostVoList: Array<AccountCostUserAllocVo>) {
    return Http.post("/api/accountCostUserAllocController/saveUpdateBatch", accountCostVoList);
}

export function findPageAccountCostUserAlloc(accountCostUserAllocVo: AccountCostUserAllocVo, pageQuery: PageQuery): Promise<TableResult<AccountCostUserAllocEntity>> {
    let params = buildPageUrlParam(accountCostUserAllocVo, pageQuery);
    return Http.get("/api/accountCostUserAllocController/findPageAccountCostUserAlloc", params);
}

export function findAccountCostUserAlloc(accountCostUserAllocVo: AccountCostUserAllocVo): Promise<R<AccountCostUserAllocEntity>> {
    let params = buildUrlParam(accountCostUserAllocVo);
    return Http.get("/api/accountCostUserAllocController/findAccountCostUserAlloc", params);
}

export function deleteAccountCostUserAlloc(idList: Array<string>) {
    return Http.post("/api/accountCostUserAllocController/deleteAccountCostUserAlloc", idList);
}

export function findListSysUserById(idList: string[]): Promise<R<FindPageSysUserListEntity[]>> {
    let params = {
        idList: idList
    };
    return Http.get('/api/accountCostUserAllocController/findListSysUserById', params)
}

export function findListAllocByCostId(costId: string): Promise<R<AccountCostUserAllocEntity[]>> {
    let params = {
        costId: costId
    };
    return Http.get('/api/accountCostUserAllocController/findListAllocByCostId', params)
}

export function findAllocAmount(): Promise<R<RpFindAllocAmountDto>> {
    return Http.post('/api/accountCostUserAllocController/findAllocAmount')
}