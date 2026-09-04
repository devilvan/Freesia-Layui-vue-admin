package com.freesia.todayhistory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 历史上的今天-查询条件。
 */
@Data
@Schema(description = "历史上的今天-查询条件")
public class TodayHistoryQueryVo {
    @Schema(description = "月份")
    private Integer monthValue;
    @Schema(description = "日期")
    private Integer dayValue;
    @Schema(description = "历史日期键")
    private String historyKey;
    @Schema(description = "页面标题")
    private String pageTitle;
}

