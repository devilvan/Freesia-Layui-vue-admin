import Http from "../Http"
import {buildPageUrlParam} from "../../util/URequest";
import {PageQuery} from "../../types/Common";

export const findCurrentUserProfile = function () {
    return Http.get('/api/sysUserController/findCurrentUserProfile')
}

export const saveUserInfo = function (user: string) {
    let params = {"encrypt": user}
    return Http.put('/api/sysUserController/saveUserInfo', params)
}

export const avatarUpdate = function (avatar: string) {
    let params = {avatar: avatar}
    return Http.post('/api/sysUserController/avatarUpdate', params, {
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    })
}

export const findPageSysUserWithoutDataScope = function (searchQuery: any, pageQuery: PageQuery) {
    const params = buildPageUrlParam(searchQuery, pageQuery);
    return Http.get('/api/sysUserController/findPageSysUserWithoutDataScope', params)
}
