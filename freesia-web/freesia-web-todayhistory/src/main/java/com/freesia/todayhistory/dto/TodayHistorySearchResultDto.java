package com.freesia.todayhistory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 历史上的今天-全局搜索结果数据传输对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "历史上的今天-全局搜索结果数据传输对象")
public class TodayHistorySearchResultDto extends TodayHistoryItemDto {
    @Schema(description = "月份")
    private Integer monthValue;
    @Schema(description = "日期")
    private Integer dayValue;
    @Schema(description = "页面标题")
    private String pageTitle;
    @Schema(description = "页面地址")
    private String pageUrl;
}
