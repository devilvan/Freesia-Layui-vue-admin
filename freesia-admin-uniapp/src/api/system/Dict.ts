import Http from "../Http";
import {R} from "../../types/Result";
import {SysDictValueEntity} from "../../types/system/Dict";

export function findCacheSysDictValueList(dictKey: string): Promise<R<SysDictValueEntity[]>> {
    let params = {dictKey: dictKey};
    return Http.get("/api/sysDictController/findCacheSysDictValueList", params);
}

export function findPageSysDictValue(searchQuery: any, pageQuery: any) {
    return Http.get("/api/sysDictController/findPageSysDictValue", {...searchQuery, ...pageQuery});
}
