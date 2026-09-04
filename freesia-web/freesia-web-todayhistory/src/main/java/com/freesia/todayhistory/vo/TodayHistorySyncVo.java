package com.freesia.todayhistory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 历史上的今天-同步请求。
 */
@Data
@Schema(description = "历史上的今天-同步请求")
public class TodayHistorySyncVo {
    @Schema(description = "月份")
    private Integer monthValue;
    @Schema(description = "日期")
    private Integer dayValue;
    @Schema(description = "历史日期键")
    private String historyKey;
    @Schema(description = "是否强制刷新")
    private Boolean forceRefresh;
}

