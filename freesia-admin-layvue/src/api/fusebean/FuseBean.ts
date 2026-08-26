import Http from "../Http";
import {R} from "@/types/Result";
import {FuseBeanConfirmReq, FuseBeanConfirmResp, FuseBeanGenerateResp} from "@/types/fusebean/FuseBean";

/**
 * 生成拼豆像素风图片
 * @param file      上传的原图（可选，纯提示词生成时需后端配置外部接口）
 * @param prompt    用户输入的提示词
 * @param gridSize  网格最大边长
 * @param maxColors 最大颜色数
 */
export function generateImage(file: File | null, prompt: string, gridSize?: number, maxColors?: number): Promise<R<FuseBeanGenerateResp>> {
    const params = new FormData();
    if (file) {
        params.append('file', file);
    }
    if (prompt) {
        params.append('prompt', prompt);
    }
    if (gridSize && gridSize > 0) {
        params.append('gridSize', String(gridSize));
    }
    if (maxColors && maxColors > 0) {
        params.append('maxColors', String(maxColors));
    }
    return Http.post('/api/fusebean/generateImage', params, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * 确认生成拼豆图纸
 */
export function confirmGenerate(req: FuseBeanConfirmReq): Promise<R<FuseBeanConfirmResp>> {
    return Http.post('/api/fusebean/confirmGenerate', req);
}
