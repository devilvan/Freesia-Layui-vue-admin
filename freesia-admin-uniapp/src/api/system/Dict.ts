import Http from "../Http";
import {R} from "../../types/Result";
import {SysDictValueEntity} from "../../types/system/Dict";

export function findListSysDictValue(dictType: string): Promise<R<SysDictValueEntity[]>> {
    let params = {dictType: dictType};
    return Http.get("/api/sysDictController/findSysDictValueList", params);
}

export function findPageSysDictValue(searchQuery: any, pageQuery: any) {
    return Http.get("/api/sysDictController/findPageSysDictValue", {...searchQuery, ...pageQuery});
}
