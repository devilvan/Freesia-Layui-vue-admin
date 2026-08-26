/**
 * 拼豆 类型定义
 */

/** 色板颜色 */
export interface FuseBeanColor {
    index?: number;
    hex?: string;
}

/** 生成拼豆像素风图片响应 */
export interface FuseBeanGenerateResp {
    /** 生成的拼豆像素风预览图（base64 PNG） */
    previewBase64?: string;
    /** 图纸宽度（格子数） */
    gridWidth?: number;
    /** 图纸高度（格子数） */
    gridHeight?: number;
    /** 色板 */
    palette?: FuseBeanColor[];
    /** 网格数据，每个格子存储色板索引 */
    grid?: number[][];
    /** 生成说明 */
    message?: string;
}

/** 确认生成拼豆图纸请求 */
export interface FuseBeanConfirmReq {
    /** 作品名称 */
    name?: string;
    /** 图纸宽度（格子数） */
    gridWidth?: number;
    /** 图纸高度（格子数） */
    gridHeight?: number;
    /** 每格渲染像素大小 */
    cellSize?: number;
    /** 色板 */
    palette?: FuseBeanColor[];
    /** 网格数据，每个格子存储色板索引 */
    grid?: number[][];
}

/** 色号统计 */
export interface FuseBeanColorStat {
    index?: number;
    hex?: string;
    /** 使用数量（颗） */
    count?: number;
}

/** 确认生成拼豆图纸响应 */
export interface FuseBeanConfirmResp {
    name?: string;
    gridWidth?: number;
    gridHeight?: number;
    cellSize?: number;
    /** 图纸网格图片（base64 PNG） */
    patternPngBase64?: string;
    /** 图纸矢量图（SVG 文本） */
    patternSvg?: string;
    /** 色号清单（购豆清单） */
    colorStats?: FuseBeanColorStat[];
}
