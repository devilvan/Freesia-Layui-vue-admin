import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import Http from "@/api/Http";
import {SysDictKeyVo, SysDictValueEntity, SysDictValueVo, SysDictVo} from "@/types/system/Dict";
import {PageQuery} from "@/types/Common";
import {TableResult} from "@/types/Result";
import {layer} from "@layui/layui-vue";

export function findSysDictKeyList(searchQuery: SysDictVo) {
    let params = buildUrlParam(searchQuery);
    return Http.get("/api/sysDictController/findSysDictKeyList", params);
}

export function findPageSysDictValue(searchQuery: SysDictVo, pageQuery: PageQuery): Promise<TableResult<SysDictValueEntity>> {
    let params = buildPageUrlParam(searchQuery, pageQuery);
    return Http.get("/api/sysDictController/findPageSysDictValue", params);
}

export async function findCacheSysDictValueList(searchQuery: SysDictVo) {
    let params = buildUrlParam(searchQuery);
    return await Http.get("/api/sysDictController/findCacheSysDictValueList", params);
}

export function saveSysDictKey(params: SysDictKeyVo) {
    return Http.post("/api/sysDictController/saveSysDictKey", params);
}

export function saveSysDictValue(params: SysDictValueVo) {
    return Http.post("/api/sysDictController/saveSysDictValue", params);
}

export function flushCacheSysDictValue(dictKey: any) {
    if ("undefined" === typeof (dictKey) || dictKey === "") {
        layer.msg("字典键不能为空！");
    } else {
        let params = {
            dictKey: dictKey
        }
        return Http.delete("/api/sysDictController/flushCacheSysDictValue", params);
    }
}

export function deleteSysDictValueList(idList: string[]) {
    return Http.put("/api/sysDictController/deleteSysDictValueList", idList);
}

export function enableSysDictValueList(idList: string[]) {
    return Http.put("/api/sysDictController/enableSysDictValueList", idList);
}

export const importSysDictValue = function (file: File, dictKey: string, keyId: string) {
    let params = {
        file: file,
        dictKey: dictKey,
        keyId: keyId
    }
    return Http.post('/api/sysDictController/importSysDictValue', params, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

export function findMaxOrderNumByKeyId(keyId: string) {
    let params = {
        keyId: keyId
    }
    return Http.get("/api/sysDictController/findMaxOrderNumByKeyId", params);
}