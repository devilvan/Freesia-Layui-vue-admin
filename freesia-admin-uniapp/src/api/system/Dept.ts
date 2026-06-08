import Http from "../Http";

export const findDeptById = function (id?: string) {
    return Http.get('/api/sysDeptController/findDeptById', id ? {id} : {})
}

export const findListSysDept = function () {
    return Http.get('/api/sysDeptController/findListSysDept')
}
