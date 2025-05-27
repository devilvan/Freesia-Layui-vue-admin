import Http from "../../../Http";
import {LaySelect, PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {
    CommonIconTemplateHeaderEntity,
    CommonIconTemplateHeaderVo
} from "@/types/common/icon/template/IconTemplateHeader";

export function saveUpdate(commonIconTemplateHeaderVo: CommonIconTemplateHeaderVo): Promise<R<void>> {
    return Http.post("/common/commonIconTemplateHeaderController/saveUpdate", commonIconTemplateHeaderVo);
}

export function saveUpdateBatch(commonIconTemplateHeaderVoList: Array<CommonIconTemplateHeaderVo>): Promise<R<void>> {
    return Http.post("/common/commonIconTemplateHeaderController/saveUpdateBatch", commonIconTemplateHeaderVoList);
}

export function findPageCommonIconTemplateHeader(commonIconTemplateHeaderVo: CommonIconTemplateHeaderVo, pageQuery: PageQuery): Promise<TableResult<CommonIconTemplateHeaderEntity>> {
    let params = buildPageUrlParam(commonIconTemplateHeaderVo, pageQuery);
    return Http.get("/common/commonIconTemplateHeaderController/findPageCommonIconTemplateHeader", params);
}

export function findCommonIconTemplateHeader(commonIconTemplateHeaderVo: CommonIconTemplateHeaderVo): Promise<R<CommonIconTemplateHeaderEntity>> {
    let params = buildUrlParam(commonIconTemplateHeaderVo);
    return Http.get("/common/commonIconTemplateHeaderController/findCommonIconTemplateHeader", params);
}

export function deleteCommonIconTemplateHeader(idList: Array<string>): Promise<R<void>> {
    return Http.post("/common/commonIconTemplateHeaderController/deleteCommonIconTemplateHeader", idList);
}

export function findMaxOrderNum(): Promise<R<number>> {
    return Http.get("/common/commonIconTemplateHeaderController/findMaxOrderNum");
}

export function findSelectCommonIconHeader(): Promise<R<LaySelect[]>> {
    return Http.get("/common/commonIconTemplateHeaderController/findSelectCommonIconHeader");
}

export function findListSelectCostType(): Promise<R<LaySelect[]>> {
    return Http.get("/common/commonIconTemplateHeaderController/findListSelectCostType");
}

