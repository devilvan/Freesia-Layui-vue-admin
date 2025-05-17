import Http from "../../../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {
    CommonIconTemplateDetailEntity,
    CommonIconTemplateDetailVo, FindMaxOrderNumVo, FindTreeIconTreeTypeEntity
} from "@/types/common/icon/template/IconTemplateDetail";

export function saveUpdate(commonIconTemplateDetailVo: CommonIconTemplateDetailVo): Promise<R<void>> {
    return Http.post("/common/commonIconTemplateDetailController/saveUpdate", commonIconTemplateDetailVo);
}

export function saveUpdateBatch(commonIconTemplateDetailVo: CommonIconTemplateDetailVo): Promise<R<void>> {
    return Http.post("/common/commonIconTemplateDetailController/saveUpdateBatch", commonIconTemplateDetailVo);
}

export function findPageCommonIconTemplateDetail(commonIconTemplateDetailVo: CommonIconTemplateDetailVo, pageQuery: PageQuery): Promise<TableResult<CommonIconTemplateDetailEntity>> {
    let params = buildPageUrlParam(commonIconTemplateDetailVo, pageQuery);
    return Http.get("/common/commonIconTemplateDetailController/findPageCommonIconTemplateDetail", params);
}

export function findCommonIconTemplateDetail(commonIconTemplateDetailVo: CommonIconTemplateDetailVo): Promise<R<CommonIconTemplateDetailEntity>> {
    let params = buildUrlParam(commonIconTemplateDetailVo);
    return Http.get("/common/commonIconTemplateDetailController/findCommonIconTemplateDetail", params);
}

export function deleteCommonIconTemplateDetail(idList: Array<string>): Promise<R<void>> {
    return Http.post("/common/commonIconTemplateDetailController/deleteCommonIconTemplateDetail", idList);
}

export function findMaxOrderNum(vo: FindMaxOrderNumVo): Promise<R<number>> {
    let params = buildUrlParam(vo);
    return Http.get("/common/commonIconTemplateDetailController/findMaxOrderNum", params);
}

export function findTreeIconTreeType(commonIconTemplateDetailVo: CommonIconTemplateDetailVo): Promise<R<Array<FindTreeIconTreeTypeEntity>>> {
    let params = buildUrlParam(commonIconTemplateDetailVo);
    return Http.get("/common/commonIconTemplateDetailController/findTreeIconTreeType", params);
}

export function findGrouping(vo: CommonIconTemplateDetailVo): Promise<R<Map<string, string>>> {
    let params = buildUrlParam(vo);
    return Http.get("/common/commonIconTemplateDetailController/findGrouping", params);
}

export function findCustomIconTemplateDetail(vo: CommonIconTemplateDetailVo): Promise<R<Record<string, FindTreeIconTreeTypeEntity[]>>> {
    let params = buildUrlParam(vo);
    return Http.get("/common/commonIconTemplateDetailController/findCustomIconTemplateDetail", params);
}