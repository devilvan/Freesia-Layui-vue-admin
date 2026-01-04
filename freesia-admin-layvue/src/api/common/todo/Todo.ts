import {CommonTodoEntity, CommonTodoVo} from "@/types/common/todo/Todo";
import {R, TableResult} from "@/types/Result";
import {PageQuery} from "@/types/Common";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import Http from "@/api/Http";

export function saveUpdate(commonTodoVo: CommonTodoVo) {
    return Http.post("/common/commonTodoController/saveUpdate", commonTodoVo);
}

export function saveUpdateBatch(commonTodoVoList: Array<CommonTodoVo>) {
    return Http.post("/common/commonTodoController/saveUpdateBatch", commonTodoVoList);
}

export function findPageCommonTodo(commonTodoVo: CommonTodoVo, pageQuery: PageQuery): Promise<TableResult<CommonTodoEntity>> {
    let params = buildPageUrlParam(commonTodoVo, pageQuery);
    return Http.get("/common/commonTodoController/findPageCommonTodo", params);
}

export function findCommonTodo(commonTodoVo: CommonTodoVo): Promise<R<CommonTodoEntity>> {
    let params = buildUrlParam(commonTodoVo);
    return Http.get("/common/commonTodoController/findCommonTodo", params);
}

export function findListCommonTodo(commonTodoVo: CommonTodoVo): Promise<R<CommonTodoEntity[]>> {
    let params = buildUrlParam(commonTodoVo);
    return Http.get("/common/commonTodoController/findListCommonTodo", params);
}

export function findCacheCommonTodoById(id: string): Promise<R<CommonTodoEntity>> {
    let params = {id: id};
    return Http.get("/common/commonTodoController/findCacheCommonTodoById", params);
}

export function deleteCommonTodo(idList: Array<string>) {
    return Http.post("/common/commonTodoController/deleteCommonTodo", idList);
}
