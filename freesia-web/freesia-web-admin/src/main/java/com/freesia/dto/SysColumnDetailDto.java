package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 数据传输对象
 * @date 2026-03-27
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
    @Schema(description = "固定（null-不固定；left-左固定；right-右固定）")
    private String fixed;
    @Schema(description = "是否过长省略（true-是；false-否）")
    private Boolean ellipsisTooltip;
    @Schema(description = "列宽（单位：px）")
    private Integer width;
    @Schema(description = "最小列宽（单位：px）")
    private Integer minWidth;
    @Schema(description = "排序号")
    private Integer orderNum;
    @Schema(description = "是否排序（null-不排序；asc-顺序；desc-倒序）")
    private String sorted;
    @Schema(description = "是否允许调整宽度（true-是；false-否）")
    private Boolean resizeFlag;
    @Schema(description = "系统列中间表ID列表")
    private List<Long> middleIdList;
    @Schema(description = "自定义插槽")
    private String customSlot;
}
