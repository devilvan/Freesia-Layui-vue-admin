import Http from "../Http"
import {AssignRoleVo, AssignDeptVo, SysUserEntity, SysUserVo} from "../../types/system/User";
import {PageQuery} from "../../types/Common";
import {buildPageUrlParam} from "../../util/URequest";
import {TableResult} from "../../types/Result";
import {SysTenantVo} from "../../types/system/Tenant";

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

export const saveUserInfo = function (user: string) {
    let params = {"encrypt": user}
    return Http.put('/api/sysUserController/saveUserInfo', params)
}

export const findUserRolesByUserId = function (userId: string) {
    let params = {userId: userId}
    return Http.get('/api/sysUserController/findUserRolesByUserId', params)
}
export const assignRole = function (assignRoleVo: AssignRoleVo) {
    return Http.post('/api/sysUserController/assignRole', assignRoleVo)
}

export const findPageUserByTenantId = function (sysTenantVo: SysTenantVo, pageQuery: PageQuery): Promise<TableResult<SysUserEntity>> {
    const params = buildPageUrlParam(sysTenantVo, pageQuery);
    return Http.get('/api/sysUserController/findPageUserByTenantId', params)
}

export function findPageAllowAssignUserByTenantId(sysTenantVo: SysTenantVo, pageQuery: PageQuery): Promise<SysUserEntity> {
    let params = buildPageUrlParam(sysTenantVo, pageQuery);
    return Http.get("/api/sysUserController/findPageAllowAssignUserByTenantId", params);
}

export const userImport = function (file: File, avatar: string) {
    let params = {
        file: file,
        avatar: avatar
    }
    return Http.post('/api/sysUserController/userImport', params, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}
export const uploadAvatar = function (file: File, id: string) {
    let params = {
        file: file,
        id: id
    }
    return Http.post('/api/sysUserController/uploadAvatar', params, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

export function findEditUserById(id: string): Promise<String> {
    let params = {id: id}
    return Http.get("/api/sysUserController/findEditUserById", params);
}

export function assignDept(assignDeptVo: AssignDeptVo) {
    return Http.post('/api/sysUserController/assignDept', assignDeptVo)
}

export function avatarUpdate(avatar: string) {
    let params = {
        avatar: avatar
    }
    return Http.post('/api/sysUserController/avatarUpdate', params, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
}