import {SysDictVo} from "@/types/system/Dict";
import {buildPageUrlParam} from "@/util/URequest";
import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {SysConfigEntity, SysConfigVo} from "@/types/system/Config";
import {R} from "@/types/Result";

export function findPageSysConfig(searchQuery: SysDictVo, pageQuery: PageQuery): Promise<SysConfigEntity> {
    let params = buildPageUrlParam(searchQuery, pageQuery);
    return Http.get("/api/sysConfigController/findPageSysConfig", params);
}

export function findConfigByKey(configKey: string): Promise<R<string>> {
    let params = {
        configKey: configKey
    }
    return Http.get("/api/sysConfigController/findConfigByKey", params);
}

export function saveConfig(sysConfigVo: SysConfigVo) {
    return Http.post("/api/sysConfigController/saveConfig", sysConfigVo);
}

export function deleteConfig(configKey: string) {
    let params = {configKey: configKey}
    return Http.delete("/api/sysConfigController/deleteConfig", params);
}
