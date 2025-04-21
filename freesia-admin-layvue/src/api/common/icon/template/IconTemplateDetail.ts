import Http from "../../../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {
    CommonIconTemplateDetailEntity,
    CommonIconTemplateDetailVo, FindTreeIconTreeTypeEntity
} from "@/types/common/icon/template/IconTemplateDetail";

export function saveUpdate(commonIconTemplateDetailVo: CommonIconTemplateDetailVo): Promise<R<void>> {
    return Http.post("/common/commonIconTemplateDetailController/saveUpdate", commonIconTemplateDetailVo);
}

export function saveUpdateBatch(commonIconTemplateDetailVoList: Array<CommonIconTemplateDetailVo>): Promise<R<void>> {
    return Http.post("/common/commonIconTemplateDetailController/saveUpdateBatch", commonIconTemplateDetailVoList);
}

export function findPageCommonIconTemplateDetail(commonIconTemplateDetailVo: CommonIconTemplateDetailVo, pageQuery: PageQuery): Promise<TableResult<CommonIconTemplateDetailEntity>> {
    let params = buildPageUrlParam(commonIconTemplateDetailVo, pageQuery);
    return Http.get("/common/commonIconTemplateDetailController/findPageCommonIconTemplateDetail", params);
}

export function findCommonIconTemplateDetail(commonIconTemplateDetailVo: CommonIconTemplateDetailVo): Promise<R<Map<String, CommonIconTemplateDetailEntity>>> {
    let params = buildUrlParam(commonIconTemplateDetailVo);
    return Http.get("/common/commonIconTemplateDetailController/findCommonIconTemplateDetail", params);
}

export function deleteCommonIconTemplateDetail(idList: Array<string>): Promise<R<void>> {
    return Http.post("/common/commonIconTemplateDetailController/deleteCommonIconTemplateDetail", idList);
}

export function findMaxOrderNum(): Promise<R<number>> {
    return Http.get("/common/commonIconTemplateDetailController/findMaxOrderNum");
}

export function findTreeIconTreeType(commonIconTemplateDetailVo: CommonIconTemplateDetailVo): Promise<R<Array<FindTreeIconTreeTypeEntity>>> {
    let params = buildUrlParam(commonIconTemplateDetailVo);
    return Http.get("/common/commonIconTemplateDetailController/findTreeIconTreeType", params);
}