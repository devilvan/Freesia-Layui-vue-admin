import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {SysColumnHeaderEntity, SysColumnHeaderVo} from "@/types/system/ColumnHeader";

export function saveUpdate(sysColumnHeaderVo: SysColumnHeaderVo): Promise<R<void>> {
    return Http.post("/api/sysColumnHeaderController/saveUpdate", sysColumnHeaderVo);
}

export function saveUpdateBatch(sysColumnHeaderVoList: Array<SysColumnHeaderVo>): Promise<R<void>> {
    return Http.post("/api/sysColumnHeaderController/saveUpdateBatch", sysColumnHeaderVoList);
}

export function findPageSysColumnHeader(sysColumnHeaderVo: SysColumnHeaderVo, pageQuery: PageQuery): Promise<TableResult<SysColumnHeaderEntity>> {
    let params = buildPageUrlParam(sysColumnHeaderVo, pageQuery);
    return Http.get("/api/sysColumnHeaderController/findPageSysColumnHeader", params);
}

export function findSysColumnHeader(sysColumnHeaderVo: SysColumnHeaderVo): Promise<R<SysColumnHeaderEntity>> {
    return Http.post("/api/sysColumnHeaderController/findSysColumnHeader", sysColumnHeaderVo);
}

export function findListSysColumnHeader(sysColumnHeaderVo: SysColumnHeaderVo): Promise<R<SysColumnHeaderEntity[]>> {
    let params = buildUrlParam(sysColumnHeaderVo);
    return Http.get("/api/sysColumnHeaderController/findListSysColumnHeader", params);
}

export function deleteSysColumnHeader(idList: Array<string>): Promise<R<void>> {
    return Http.post("/api/sysColumnHeaderController/deleteSysColumnHeader", idList);
}