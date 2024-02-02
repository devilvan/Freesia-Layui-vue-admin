import Http from "../Http";
import {PageQuery} from "../../types/Common";
import {R, TableResult} from "../../types/Result";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {SysTenantEntity, SysTenantVo} from "../../types/system/Tenant";

export function saveUpdate(sysTenantVo: SysTenantVo) {
    return Http.post("/api/sysTenantController/saveUpdate", sysTenantVo);
}

export function saveUpdateBatch(sysTenantVoList: Array<SysTenantVo>) {
    return Http.post("/api/sysTenantController/saveUpdateBatch", sysTenantVoList);
}

export function findPageSysTenant(sysTenantVo: SysTenantVo, pageQuery: PageQuery): Promise<TableResult<SysTenantEntity>> {
    let params = buildPageUrlParam(sysTenantVo, pageQuery);
    return Http.get("/api/sysTenantController/findPageSysTenant", params);
}

export function findSysTenant(sysTenantVo: SysTenantVo): Promise<R<SysTenantEntity>> {
    let params = buildUrlParam(sysTenantVo);
    return Http.get("/api/sysTenantController/findSysTenant", params);
}

export function deleteSysTenant(id: string) {
    let params = {id: id};
    return Http.delete("/api/sysTenantController/deleteSysTenant", params);
}
