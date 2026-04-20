package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Bliss.Wu
 * @Description 默认系统列 值对象
 * @date 2026-03-20
 */
@Data
public class DefaultColumnVo {
    @Schema(description = "列名")
    private String title;
    @Schema(description = "属性名")
    private String key;
    @Schema(description = "hide")
    @JsonAlias(value = "hide")
    private Boolean enabled;
    @Schema(description = "宽度（单位：px）")
    private Integer width;
    @Schema(description = "最小宽度（单位：px）")
    private Integer minWidth;
    @Schema(description = "是否排序（null-不排序；A-顺序；D-倒序）")
    private String sorted;
    @Schema(description = "是否过长省略（true-是；false-否）")
    private Boolean ellipsisTooltip;
    @Schema(description = "是否固定（left-左固定；right-右固定）")
    private String fixed;
    @Schema(description = "是否允许拖动（true-是；false-否）")
    private Boolean resizeFlag;
    @Schema(description = "自定义插槽")
    private String customSlot;
}
