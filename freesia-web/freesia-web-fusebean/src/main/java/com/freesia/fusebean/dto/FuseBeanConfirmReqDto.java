package com.freesia.fusebean.dto;

import com.freesia.fusebean.vo.FuseBeanColorVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 确认生成拼豆图纸请求
 * @date 2026-08-26
 */
@Data
@Schema(description = "确认生成拼豆图纸请求")
public class FuseBeanConfirmReqDto {

    @Schema(description = "作品名称")
    private String name;

    @Schema(description = "图纸宽度（格子数）")
    private Integer gridWidth;

    @Schema(description = "图纸高度（格子数）")
    private Integer gridHeight;

    @Schema(description = "每格渲染像素大小，默认 14")
    private Integer cellSize;

    @Schema(description = "色板")
    private List<FuseBeanColorVo> palette;

    @Schema(description = "网格数据，每个格子存储色板索引")
    private List<List<Integer>> grid;
}
