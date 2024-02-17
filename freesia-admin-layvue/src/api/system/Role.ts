import {PageQuery} from "../../types/Common";
import Http from "../Http";
import {AssignUserVo, SaveRoleMenuPrivilegeVo, SysRoleVo} from "../../types/system/Role";
import {buildPageUrlParam} from "../../util/URequest";

export const findPageSysRoleList = function (searchQuery: SysRoleVo, pageQuery: PageQuery) {
    const params = buildPageUrlParam(searchQuery, pageQuery)
    return Http.get('/api/sysRoleController/findPageSysRoleList', params)
}

export function saveRoleMenuPrivilege(vo: SaveRoleMenuPrivilegeVo) {
    let params = {
        ...vo
    }
    return Http.post("/api/sysRoleController/saveRoleMenuPrivilege", params)
}

export const findRoleById = function (roleId: any) {
    const params = {roleId: roleId}
    return Http.get('/api/sysRoleController/findRoleById', params)
}

export const findAllRoles = function () {
    return Http.get('/api/sysRoleController/findAllRoles');
}

export const findPageUserByRoleId = function (searchQuery: SysRoleVo, pageQuery: PageQuery) {
    const params = buildPageUrlParam(searchQuery, pageQuery)
    return Http.get('/api/sysRoleController/findPageUserByRoleId', params)
}

export const findPageAllowAssignUserByRoleId = function (searchQuery: SysRoleVo, pageQuery: PageQuery) {
    const params = buildPageUrlParam(searchQuery, pageQuery)
    return Http.get('/api/sysRoleController/findPageAllowAssignUserByRoleId', params)
}

export function assignUser(vo: AssignUserVo) {
    let params = {
        ...vo
    }
    return Http.post("/api/sysRoleController/assignUser", params)
}

export function cancelAssignUser(vo: AssignUserVo) {
    let params = {
        ...vo
    }
    return Http.post("/api/sysRoleController/cancelAssignUser", params)
}
