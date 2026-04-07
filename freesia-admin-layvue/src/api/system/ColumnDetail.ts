import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {SysColumnDetailEntity, SysColumnDetailVo} from "@/types/system/ColumnDetail";

export function saveUpdate(sysColumnDetailVo: SysColumnDetailVo): Promise<R<void>> {
 return Http.post("/api/sysColumnDetailController/saveUpdate", sysColumnDetailVo);
}

export function saveUpdateBatch(sysColumnDetailVoList: Array<SysColumnDetailVo>): Promise<R<void>> {
 return Http.post("/api/sysColumnDetailController/saveUpdateBatch", sysColumnDetailVoList);
}

export function findPageSysColumnDetail(sysColumnDetailVo: SysColumnDetailVo, pageQuery: PageQuery): Promise<TableResult<SysColumnDetailEntity>> {
 let params = buildPageUrlParam(sysColumnDetailVo, pageQuery);
 return Http.get("/api/sysColumnDetailController/findPageSysColumnDetail", params);
}

export function findSysColumnDetail(sysColumnDetailVo: SysColumnDetailVo): Promise<R<SysColumnDetailEntity>> {
 let params = buildUrlParam(sysColumnDetailVo);
 return Http.get("/api/sysColumnDetailController/findSysColumnDetail", params);
}

export function findListSysColumnDetail(sysColumnDetailVo: SysColumnDetailVo): Promise<R<SysColumnDetailEntity>> {
 let params = buildUrlParam(sysColumnDetailVo);
 return Http.get("/api/sysColumnDetailController/findListSysColumnDetail", params);
}

export function deleteSysColumnDetail(idList: Array<string>): Promise<R<void>> {
 return Http.post("/api/sysColumnDetailController/deleteSysColumnDetail", idList);
}

export function toggleEnabled(id: string): Promise<R<void>> {
 let param: SysColumnDetailVo = {id: id,}
 return Http.post("/api/sysColumnDetailController/toggleEnabled", param);
}