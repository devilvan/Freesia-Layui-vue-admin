import Http from "../Http"
import {AssignRoleVo, AssignDeptVo, SysUserEntity, SysUserVo, FindPageSysUserListEntity} from "../../types/system/User";
import {PageQuery} from "../../types/Common";
import {buildPageUrlParam} from "../../util/URequest";
import {R, TableResult} from "../../types/Result";
import {SysTenantVo} from "../../types/system/Tenant";
import {List} from "echarts";

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

export function findEditUserById(id: string): Promise<String> {
    let params = {id: id}
    return Http.get("/api/sysUserController/findEditUserById", params);
}

export function deleteUser(idList: Array<string>) {
    return Http.post('/api/sysUserController/deleteUser', idList)
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

export const findPageSysUserWithoutDataScope = function (searchQuery: SysUserVo, pageQuery: PageQuery) : Promise<TableResult<FindPageSysUserListEntity>>{
    const params = buildPageUrlParam(searchQuery, pageQuery);
    return Http.get('/api/sysUserController/findPageSysUserWithoutDataScope', params)
}

export function findListSysUserById(idList: string[]) : Promise<R<FindPageSysUserListEntity[]>>{
    let params = {
        idList: idList
    };
    return Http.get('/api/sysUserController/findListSysUserById', params)
}