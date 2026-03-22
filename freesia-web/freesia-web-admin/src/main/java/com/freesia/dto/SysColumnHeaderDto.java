package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 系统列头表 数据传输对象
 * @date 2026-03-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统列头表 数据传输对象")
public class SysColumnHeaderDto extends BaseDto {
    @Schema(description = "组件ID")
    private String componentId;
    @Schema(description = "组件名")
    private String name;
    @Schema(description = "表格高度")
    private Integer height;
    @Schema(description = "表格最大高度")
    private Integer maxHeight;
    @Schema(description = "初始化分页大小")
    private Integer initPageSize;
    @Schema(description = "是否启用（true-是；false-否）")
    private Boolean enabled;
    @Schema(description = "是否允许单元格列宽拖动（true-是；false-否）")
    private Boolean resizeFlag;
    @Schema(description = "是否允许根据内容自动计算列宽（true-是；false-否）")
    private Boolean autoColsWidthFlag;
    @Schema(description = "是否启用默认工具栏（true-是；false-否）")
    private Boolean defaultToolBarFlag;
    @Schema(description = "组件名")
    private String component;
    @Schema(description = "系统列明细")
    private List<SysColumnDetailDto> sysColumnDetailDtoList;
}
