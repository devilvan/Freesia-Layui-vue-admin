import Http from "../Http";
import {AssignButtonVo, SysMenuEntity, SysMenuVo} from "../../types/system/Menu";
import {R} from "../../types/Result";
import {buildUrlParam} from "../../util/URequest";

export function findSelectedMenuListByRoleId(roleId: string) {
    let params = {
        roleId: roleId
    }
    return Http.get("/api/sysMenuController/findSelectedMenuListByRoleId", params);
}

export function findAllMenuTree() {
    return Http.get("/api/sysMenuController/findAllMenuTree");
}

export function findMenuListByUserId() {
    return Http.get("/api/sysMenuController/findMenuListByUserId")
}

export function findTreeMenuSelect(menuType: any) {
    let params = {
        menuType: menuType
    }
    return Http.get("/api/sysMenuController/findTreeMenuSelect", params)
}

export function saveMenu(form: SysMenuVo) {
    return Http.post("/api/sysMenuController/saveMenu", form)
}

export function deleteMenu(id: any) {
    let params = {
        id: id
    }
    return Http.delete("/api/sysMenuController/deleteMenu", params)
}

export function findAllSysButton(searchQuery: SysMenuVo): Promise<R<SysMenuEntity>> {
    let params = buildUrlParam(searchQuery);
    return Http.get("/api/sysMenuController/findAllSysButton", params);
}

export function findAssignedSysButtonByRoleId(searchQuery: SysMenuVo, roleId: string): Promise<R<Number>> {
    let params = buildUrlParam(searchQuery);
    params['roleId'] = roleId;
    return Http.get("/api/sysMenuController/findAssignedSysButtonByRoleId", params);
}

export function assignButton(assignButtonVo: AssignButtonVo) {
    return Http.post("/api/sysMenuController/assignButton", assignButtonVo)
}
