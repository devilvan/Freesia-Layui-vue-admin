import {SysDictVo} from "../../types/system/Dict";
import {buildPageUrlParam} from "../../util/URequest";
import Http from "../Http";
import {PageQuery} from "../../types/Common";
import {SysConfigEntity} from "../../types/system/Config";

export function findCaptchaEnabled() {
    return Http.get("/api/sysConfigController/findCaptchaEnabled");
}

export function findPageSysConfig(searchQuery: SysDictVo, pageQuery: PageQuery): Promise<SysConfigEntity> {
    let params = buildPageUrlParam(searchQuery, pageQuery);
    return Http.get("/api/sysConfigController/findPageSysConfig", params);
}
