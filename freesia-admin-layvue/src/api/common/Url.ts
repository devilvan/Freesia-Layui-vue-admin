import Http from "../Http";
import {UrlConfigEntity, UrlConfigVo} from "../../types/common/Url";
import {PageQuery} from "../../types/Common";
import {buildPageUrlParam, buildUrlParam} from "../../util/URequest";
import {R, TableResult} from "../../types/Result";

export function saveUpdate(urlConfigVo: UrlConfigVo) {
    return Http.post("/common/urlConfigController/saveUpdate", urlConfigVo);
}

export function saveUpdateBatch(urlConfigVoList: Array<UrlConfigVo>) {
    return Http.post("/common/urlConfigController/saveUpdateBatch", urlConfigVoList);
}

export function findPageUrlConfig(urlConfigVo: UrlConfigVo, pageQuery: PageQuery): Promise<TableResult<UrlConfigEntity>> {
    let params = buildPageUrlParam(urlConfigVo, pageQuery);
    return Http.get("/common/urlConfigController/findPageUrlConfig", params);
}

export function findUrlConfig(urlConfigVo: UrlConfigVo): Promise<R<UrlConfigEntity>> {
    let params = buildUrlParam(urlConfigVo);
    return Http.get("/common/urlConfigController/findUrlConfig", params);
}

export function findCacheUrlConfigById(id: string): Promise<R<UrlConfigEntity>> {
    let params = {id: id};
    return Http.get("/common/urlConfigController/findCacheUrlConfigById", params);
}

export function deleteUrlConfig(id: string, code: string) {
    let params = {id: id, code: code};
    return Http.delete("/common/urlConfigController/deleteUrlConfig", params);
}
