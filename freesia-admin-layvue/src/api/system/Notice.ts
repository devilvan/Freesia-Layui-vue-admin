import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {SysNoticeEntity, SysNoticeVo} from "@/types/system/Notice";

export function findPageSysNotice(searchQuery: SysNoticeVo, pageQuery: PageQuery): Promise<TableResult<SysNoticeEntity>> {
    let params = buildPageUrlParam(searchQuery, pageQuery);
    return Http.get("/api/sysNoticeController/findPageSysNotice", params);
}

export function findSysNotice(searchQuery: SysNoticeVo): Promise<R<SysNoticeEntity>> {
    let params = buildUrlParam(searchQuery);
    return Http.get("/api/sysNoticeController/findSysNotice", params);
}

export function saveUpdate(sysNoticeVo: SysNoticeVo) {
    return Http.post("/api/sysNoticeController/saveUpdate", sysNoticeVo);
}

export function deleteSysNotice(idList: string[]) {
    return Http.delete("/api/sysNoticeController/deleteSysNotice", idList);
}
