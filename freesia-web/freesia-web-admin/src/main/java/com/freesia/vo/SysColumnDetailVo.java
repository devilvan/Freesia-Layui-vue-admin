package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 系统列明细表 值对象
 * @date 2026-03-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统列明细表 值对象")
public class SysColumnDetailVo extends BaseVo {
    @Schema(description = "用户ID")
    @JsonAlias(value = {"userId"})
    private Long userId;
    @Schema(description = "系统列头ID")
    @JsonAlias(value = {"headerId"})
    private Long headerId;
    @Schema(description = "系统列中间表ID")
    @JsonAlias(value = {"middleId"})
    private Long middleId;
    @Schema(description = "列名")
    @JsonAlias(value = {"title"})
    private String title;
    @Schema(description = "属性名")
    @JsonAlias(value = {"name"})
    private String name;
    @Schema(description = "是否启用（true-是；false-否）")
    @JsonAlias(value = {"enabled"})
    private Boolean enabled;
    @Schema(description = "固定（null-不固定；left-左固定；right-右固定）")
    @JsonAlias(value = {"fixed"})
    private String fixed;
    @Schema(description = "是否过长省略（true-是；false-否）")
    @JsonAlias(value = {"ellipsisTooltip"})
    private Boolean ellipsisTooltip;
    @Schema(description = "列宽（单位：px）")
    @JsonAlias(value = {"width"})
    private Integer width;
    @Schema(description = "最小列宽（单位：px）")
    @JsonAlias(value = {"minWidth"})
    private Integer minWidth;
    @Schema(description = "排序号")
    @JsonAlias(value = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "是否排序（null-不排序；asc-顺序；desc-倒序）")
    @JsonAlias(value = {"sorted"})
    private String sorted;
    @Schema(description = "是否允许调整宽度（true-是；false-否）")
    @JsonAlias(value = {"resizeFlag"})
    private Boolean resizeFlag;
}
