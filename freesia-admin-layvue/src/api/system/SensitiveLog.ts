import {PageQuery} from "../../types/Common";
import {buildPageUrlParam} from "../../util/URequest";
import Http from "../Http";
import {SysSensitiveLogVo} from "../../types/system/SensitiveLog";

export const findPageLoginLog = function (searchQuery: SysSensitiveLogVo, pageQuery: PageQuery) {
    const params = buildPageUrlParam(searchQuery, pageQuery)
    return Http.get('/api/sysSensitiveLogController/findPageLoginLog', params)
}

export const findPageOptionLog = function (searchQuery: SysSensitiveLogVo, pageQuery: PageQuery) {
    const params = buildPageUrlParam(searchQuery, pageQuery)
    return Http.get('/api/sysSensitiveLogController/findPageOptionLog', params)
}
