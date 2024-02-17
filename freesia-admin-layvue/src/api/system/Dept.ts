import Http from "../Http";
import {SysDeptVo} from "../../types/system/Dept";
import {PageQuery} from "../../types/Common";
import {buildPageUrlParam} from "../../util/URequest";

export function findDeptTreeList(searchQuery: SysDeptVo, pageQuery: PageQuery) {
    let params = buildPageUrlParam(searchQuery, pageQuery);
    return Http.get("/api/sysDeptController/findDeptTreeList", params);
}

export const findDeptById = function () {
    return Http.get('/api/sysDeptController/findDeptById')
}
