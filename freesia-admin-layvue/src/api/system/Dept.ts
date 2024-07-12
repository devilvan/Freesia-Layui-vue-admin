import Http from "../Http";
import {SysDeptVo} from "../../types/system/Dept";
import {PageQuery} from "../../types/Common";
import {buildPageUrlParam} from "../../util/URequest";
import {R} from "../../types/Result";

export function findDeptTreeList(searchQuery: SysDeptVo, pageQuery: PageQuery) {
    let params = buildPageUrlParam(searchQuery, pageQuery);
    return Http.get("/api/sysDeptController/findDeptTreeList", params);
}

export const findDeptById = function () {
    return Http.get('/api/sysDeptController/findDeptById')
}

export function findIncrementOrderNum(parentId: string): Promise<R<String>> {
    let params = {parentId: parentId}
    return Http.get("/api/sysDeptController/findIncrementOrderNum", params);
}

export function findTreeDeptSelect() {
    return Http.get("/api/sysDeptController/findTreeDeptSelect");
}

export function findTreeAssignDeptSelect() {
    return Http.get("/api/sysDeptController/findTreeAssignDeptSelect");
}

export function saveDept(encrypt: string) {
    let param = {encrypt: encrypt}
    return Http.post("/api/sysDeptController/saveDept", param);
}

