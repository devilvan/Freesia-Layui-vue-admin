package com.freesia.fusebean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 确认生成拼豆图纸响应
 * @date 2026-08-26
 */
@Data
@Schema(description = "确认生成拼豆图纸响应")
public class FuseBeanConfirmRespVo {

    @Schema(description = "作品名称")
    private String name;

    @Schema(description = "图纸宽度（格子数）")
    private Integer gridWidth;

    @Schema(description = "图纸高度（格子数）")
    private Integer gridHeight;

    @Schema(description = "每格渲染像素大小")
    private Integer cellSize;

    @Schema(description = "图纸网格图片（base64 PNG，data:image/png;base64,...）")
    private String patternPngBase64;

    @Schema(description = "纯净图纸图片（base64 PNG，仅色块，无坐标/色码/图例）")
    private String patternPngCleanBase64;

    @Schema(description = "图纸矢量图（SVG 文本）")
    private String patternSvg;

    @Schema(description = "色号清单（购豆清单）")
    private List<FuseBeanColorStatVo> colorStats;
}
