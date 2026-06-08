import Http from "../Http";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {PageQuery} from "../../types/Common";
import {R, TableResult} from "../../types/Result";
import {MarkReadVo, SysNoticeEntity, SysNoticeVo} from "../../types/system/Notice";

export function findPageSysNotice(searchQuery: SysNoticeVo, pageQuery: PageQuery): Promise<TableResult<SysNoticeEntity>> {
    let params = buildPageUrlParam(searchQuery, pageQuery);
    return Http.get("/api/sysNoticeController/findPageSysNotice", params);
}

export function findListSysNotice(searchQuery: SysNoticeVo): Promise<R<SysNoticeEntity[]>> {
    let params = buildUrlParam(searchQuery);
    return Http.get("/api/sysNoticeController/findListSysNotice", params);
}

export function findSysNotice(searchQuery: SysNoticeVo): Promise<R<SysNoticeEntity>> {
    let params = buildUrlParam(searchQuery);
    return Http.get("/api/sysNoticeController/findSysNotice", params);
}

export function saveUpdate(sysNoticeVo: SysNoticeVo) {
    return Http.post("/api/sysNoticeController/saveUpdate", sysNoticeVo);
}

export function deleteSysNotice(idList: string[]) {
    return Http.post("/api/sysNoticeController/deleteSysNotice", idList);
}

export function findPublishedAnnouncement(): Promise<R<SysNoticeEntity[]>> {
    return Http.get("/api/sysNoticeController/findPublishedAnnouncement");
}

export function findUnreadCount(searchQuery: SysNoticeVo): Promise<R<number>> {
    let params = buildUrlParam(searchQuery);
    return Http.get("/api/sysNoticeController/findUnreadCount", params);
}

export function markRead(markReadVo: MarkReadVo): Promise<R<number>> {
    return Http.post("/api/sysNoticeController/markRead", markReadVo);
}
