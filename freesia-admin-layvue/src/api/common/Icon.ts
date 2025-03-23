import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {CommonIconEntity, CommonIconVo} from "@/types/common/Icon";

export function saveUpdate(fileList: File[], commonIconVo: CommonIconVo): Promise<R<void>> {
    let param = {
        fileList: fileList,
        commonIconVo: commonIconVo
    }
    return Http.post("/common/commonIconController/saveUpdate", param, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

export function saveUpdateBatch(commonIconVoList: Array<CommonIconVo>): Promise<R<void>> {
    return Http.post("/common/commonIconController/saveUpdateBatch", commonIconVoList);
}

export function findPageCommonIcon(commonIconVo: CommonIconVo, pageQuery: PageQuery): Promise<TableResult<CommonIconEntity>> {
    let params = buildPageUrlParam(commonIconVo, pageQuery);
    return Http.get("/common/commonIconController/findPageCommonIcon", params);
}

export function findCommonIcon(commonIconVo: CommonIconVo): Promise<R<CommonIconEntity>> {
    let params = buildUrlParam(commonIconVo);
    return Http.get("/common/commonIconController/findCommonIcon", params);
}

export function deleteCommonIcon(idList: Array<string>): Promise<R<void>> {
    return Http.post("/common/commonIconController/deleteCommonIcon", idList);
}