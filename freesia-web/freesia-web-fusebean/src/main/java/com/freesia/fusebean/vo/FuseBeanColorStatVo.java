package com.freesia.fusebean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Evad.Wu
 * @Description 拼豆色号统计（购豆清单条目）
 * @date 2026-08-26
 */
@Data
@Schema(description = "拼豆色号统计")
public class FuseBeanColorStatVo {

    @Schema(description = "颜色索引")
    private Integer index;

    @Schema(description = "颜色十六进制值，如 #FF0000")
    private String hex;

    @Schema(description = "使用数量（颗）")
    private Integer count;
}
