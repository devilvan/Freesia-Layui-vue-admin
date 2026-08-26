package com.freesia.fusebean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 生成拼豆像素风图片响应
 * @date 2026-08-26
 */
@Data
@Schema(description = "生成拼豆像素风图片响应")
public class FuseBeanGenerateRespVo {

    @Schema(description = "生成的拼豆像素风预览图（base64 PNG，data:image/png;base64,...）")
    private String previewBase64;

    @Schema(description = "图纸宽度（格子数）")
    private Integer gridWidth;

    @Schema(description = "图纸高度（格子数）")
    private Integer gridHeight;

    @Schema(description = "色板")
    private List<FuseBeanColorVo> palette;

    @Schema(description = "网格数据，每个格子存储色板索引")
    private List<List<Integer>> grid;

    @Schema(description = "生成说明")
    private String message;
}
