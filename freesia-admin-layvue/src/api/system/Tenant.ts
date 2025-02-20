import Http from "../Http";
import {PageQuery} from "../../types/Common";
import {TableResult} from "../../types/Result";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {AssignTenantVo, SysTenantEntity, SysTenantVo} from "../../types/system/Tenant";

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

export function findSysTenant(sysTenantVo: SysTenantVo): Promise<SysTenantEntity> {
    let params = buildUrlParam(sysTenantVo);
    return Http.get("/api/sysTenantController/findSysTenant", params);
}

export function deleteSysTenant(idList: Array<string>) {
    return Http.post("/api/sysTenantController/deleteSysTenant", idList);
}

export function assignTenant(assignTenantVo: AssignTenantVo) {
    return Http.post("/api/sysTenantController/assignTenant", assignTenantVo);
}

export function cancelTenantAssignUser(assignTenantVo: AssignTenantVo) {
    return Http.post("/api/sysTenantController/cancelTenantAssignUser", assignTenantVo);
}

export function reloadSysTenant() {
    return Http.put('/api/sysTenantController/reloadSysTenant')
}
