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
 * @Description 系统列头表 值对象
 * @date 2026-03-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统列头表 值对象")
public class SysColumnHeaderVo extends BaseVo {
    @Schema(description = "组件ID")
    @JsonAlias(value = {"componentId"})
    private String componentId;
    @Schema(description = "组件名")
    @JsonAlias(value = {"name"})
    private String name;
    @Schema(description = "表格高度")
    @JsonAlias(value = {"height"})
    private Integer height;
    @Schema(description = "表格最大高度")
    @JsonAlias(value = {"maxHeight"})
    private Integer maxHeight;
    @Schema(description = "初始化分页大小")
    @JsonAlias(value = {"initPageSize"})
    private Integer initPageSize;
    @Schema(description = "是否启用（true-是；false-否）")
    @JsonAlias(value = {"enabled"})
    private Boolean enabled;
    @Schema(description = "是否允许单元格列宽拖动（true-是；false-否）")
    @JsonAlias(value = {"resizeFlag"})
    private Boolean resizeFlag;
    @Schema(description = "是否允许根据内容自动计算列宽（true-是；false-否）")
    @JsonAlias(value = {"autoColsWidthFlag"})
    private Boolean autoColsWidthFlag;
    @Schema(description = "是否启用默认工具栏（true-是；false-否）")
    @JsonAlias(value = {"defaultToolBarFlag"})
    private Boolean defaultToolBarFlag;
    @Schema(description = "组件名")
    @JsonAlias(value = {"component"})
    private String component;
}
