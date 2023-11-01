import {PageQuery} from "../../types/Common";
import Http from "../Http";
import {SaveRoleMenuPrivilegeVo, SysRoleVo} from "../../types/system/Role";
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
