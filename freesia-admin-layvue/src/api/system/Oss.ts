import Http from "../Http";
import {PageQuery} from "../../types/Common";
import {TableResult} from "../../types/Result";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {SysOssEntity, SysOssVo} from "../../types/system/Oss";

export function saveUpdate(sysOssVo: SysOssVo) {
    return Http.post("/common/sysOssController/saveUpdate", sysOssVo);
}

export function saveUpdateBatch(sysOssVoList: Array<SysOssVo>) {
    return Http.post("/common/sysOssController/saveUpdateBatch", sysOssVoList);
}

export function findPageSysOss(sysOssVo: SysOssVo, pageQuery: PageQuery): Promise<TableResult<SysOssEntity>> {
    let params = buildPageUrlParam(sysOssVo, pageQuery);
    return Http.get("/common/sysOssController/findPageSysOss", params);
}

export function findSysOss(sysOssVo: SysOssVo): Promise<SysOssEntity> {
    let params = buildUrlParam(sysOssVo);
    return Http.get("/common/sysOssController/findSysOss", params);
}

export function deleteSysOss(idList: Array<string>) {
    return Http.post("/common/sysOssController/deleteSysOss", idList);
}

export function upload(fileList: any[]) {
    return Http.post("/common/sysOssController/upload", fileList);
}

