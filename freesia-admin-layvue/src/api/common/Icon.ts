import Http from "../Http";
import {PageQuery} from "@/types/Common";
import {R, TableResult} from "@/types/Result";
import {buildPageUrlParam, buildUrlParam} from "@/util/URequest";
import {
    CommonIconEntity,
    CommonIconSaveUpdateEntity,
    CommonIconVo,
    FindCommonIconEntity,
    FindPageCommonIconEntity
} from "@/types/common/Icon";

export function saveUpdate(fileList: File[], commonIconVo: CommonIconVo): Promise<R<CommonIconSaveUpdateEntity>> {
    let param = {
        file: fileList,
        commonIconVo: JSON.stringify(commonIconVo)
    }
    return Http.post("/common/commonIconController/saveUpdate", param, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

export function saveUpdateBatch(fileList: File[], commonIconVo: CommonIconVo): Promise<R<CommonIconSaveUpdateEntity>> {
    let param = {
        file: fileList,
        commonIconVo: JSON.stringify(commonIconVo)
    }
    return Http.post("/common/commonIconController/saveUpdateBatch", param, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

export function findPageCommonIcon(commonIconVo: CommonIconVo, pageQuery: PageQuery): Promise<TableResult<FindPageCommonIconEntity>> {
    let params = buildPageUrlParam(commonIconVo, pageQuery);
    return Http.get("/common/commonIconController/findPageCommonIcon", params);
}

export function findCommonIcon(commonIconVo: CommonIconVo): Promise<R<FindCommonIconEntity>> {
    let params = buildUrlParam(commonIconVo);
    return Http.get("/common/commonIconController/findCommonIcon", params);
}

export function deleteCommonIcon(idList: Array<string>): Promise<R<void>> {
    return Http.post("/common/commonIconController/deleteCommonIcon", idList);
}