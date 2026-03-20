package com.freesia.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 数据传输对象
 * @date 2026-03-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统列明细表 数据传输对象")
public class SysColumnDetailDto extends BaseDto {
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "系统列头ID")
    private Long headerId;
    @Schema(description = "系统列中间表ID")
    private Long middleId;
    @Schema(description = "列名")
    private String title;
    @Schema(description = "属性名")
    private String name;
    @Schema(description = "是否启用（true-是；false-否）")
    private Boolean enabled;
    @Schema(description = "是否固定（true-是；false-否）")
    private Boolean fixed;
    @Schema(description = "是否过长省略（true-是；false-否）")
    private Boolean ellipsisTooltip;
    @Schema(description = "列宽（单位：px）")
    private Integer width;
    @Schema(description = "最小列宽（单位：px）")
    private Integer minWidth;
    @Schema(description = "最大列宽（单位：px）")
    private Integer maxWidth;
    @Schema(description = "排序号")
    private Integer orderNum;
    @Schema(description = "是否排序（null-不排序；A-顺序；D-倒序）")
    private String sorted;
    @Schema(description = "是否允许拖动（true-是；false-否）")
    private Boolean resizeFlag;
}
