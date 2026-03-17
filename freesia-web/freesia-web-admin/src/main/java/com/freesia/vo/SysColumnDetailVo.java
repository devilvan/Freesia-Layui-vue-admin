package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 值对象
 * @date 2026-03-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统列明细表 值对象")
public class SysColumnDetailVo extends BaseVo {
    @Schema(description = "用户ID")
    @JsonAlias(value = {"userId"})
    private Long userId;
    @Schema(description = "系统列头ID")
    @JsonAlias(value = {"headerId"})
    private Long headerId;
    @Schema(description = "列名")
    @JsonAlias(value = {"title"})
    private String title;
    @Schema(description = "是否启用（true-是；false-否）")
    @JsonAlias(value = {"enabled"})
    private Boolean enabled;
    @Schema(description = "是否固定（true-是；false-否）")
    @JsonAlias(value = {"fixed"})
    private Boolean fixed;
    @Schema(description = "是否过长省略（true-是；false-否）")
    @JsonAlias(value = {"ellipsisTooltip"})
    private Boolean ellipsisTooltip;
    @Schema(description = "列宽（单位：px）")
    @JsonAlias(value = {"width"})
    private Integer width;
    @Schema(description = "最小列宽（单位：px）")
    @JsonAlias(value = {"minWidth"})
    private Integer minWidth;
    @Schema(description = "最大列宽（单位：px）")
    @JsonAlias(value = {"maxWidth"})
    private Integer maxWidth;
    @Schema(description = "排序号")
    @JsonAlias(value = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "是否排序（null-不排序；A-顺序；D-倒序）")
    @JsonAlias(value = {"sorted"})
    private String sorted;
}
