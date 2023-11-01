import Http from "../Http";
import {FindMenuListByUserIdEntity, SysMenuVo} from "../../types/system/Menu";

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
