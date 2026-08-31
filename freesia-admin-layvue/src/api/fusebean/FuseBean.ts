import Http from "../Http";
import {R} from "@/types/Result";
import {FuseBeanConfirmReq, FuseBeanConfirmResp, FuseBeanGenerateResp} from "@/types/fusebean/FuseBean";

export interface FuseBeanGenerateOptions {
    processingMode?: 'edge' | 'average' | 'dominant';
    removeBackground?: boolean;
    flipHorizontal?: boolean;
    /** AI 风格重绘提示词，填写后先由 gpt-image-2 重绘原图再像素化 */
    aiStylePrompt?: string;
}

/**
 * 生成拼豆像素风图片
 */
export function generateImage(
    file: File | null,
    prompt: string,
    gridSize?: number,
    maxColors?: number,
    options?: FuseBeanGenerateOptions
): Promise<R<FuseBeanGenerateResp>> {
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
    if (options?.processingMode) {
        params.append('processingMode', options.processingMode);
    }
    if (options?.removeBackground !== undefined) {
        params.append('removeBackground', String(options.removeBackground));
    }
    if (options?.flipHorizontal !== undefined) {
        params.append('flipHorizontal', String(options.flipHorizontal));
    }
    if (options?.aiStylePrompt) {
        params.append('aiStylePrompt', options.aiStylePrompt);
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
