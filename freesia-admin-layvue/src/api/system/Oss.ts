import Http from "../Http";
import {PageQuery} from "../../types/Common";
import {R, TableResult} from "../../types/Result";
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

export function upload(file: File[]) {
    let params = {
        file: file,
    }
    return Http.post('/common/sysOssController/upload', params, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

export function uploadTemp(file: File[]): Promise<R<SysOssEntity>> {
    let params = {
        file: file,
    }
    return Http.post('/common/sysOssController/uploadTemp', params, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

