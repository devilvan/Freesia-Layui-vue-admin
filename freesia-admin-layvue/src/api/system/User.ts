import Http from "../Http"
import {SysUserVo} from "../../types/system/User";
import {PageQuery} from "../../types/Common";
import {buildPageUrlParam} from "../../util/URequest";

export const findPageSysUserList = function (searchQuery: SysUserVo, pageQuery: PageQuery) {
    const params = buildPageUrlParam(searchQuery, pageQuery);
    return Http.get('/api/sysUserController/findPageSysUserList', params)
}

export const findPageSysUserByDept = function (searchQuery: SysUserVo, pageQuery: PageQuery) {
    const params = buildPageUrlParam(searchQuery, pageQuery);
    return Http.get('/api/sysUserController/findPageSysUserByDept', params)
}

export const findCurrentUserProfile = function () {
    return Http.get('/api/sysUserController/findCurrentUserProfile')
}

export const saveUserInfo = function (sysUserVo: SysUserVo) {
    return Http.put('/api/sysUserController/saveUserInfo', sysUserVo)
}
