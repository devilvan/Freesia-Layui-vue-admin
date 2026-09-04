package com.freesia.todayhistory.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 历史上的今天-页面表 数据传输对象
 * @date 2026-09-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "历史上的今天-页面表 数据传输对象")
public class TodayHistoryPageDto extends BaseDto {
    @Schema(description = "月份")
    private Integer monthValue;
    @Schema(description = "日期")
    private Integer dayValue;
    @Schema(description = "历史日期键（MM-DD）")
    private String historyKey;
    @Schema(description = "页面标题")
    private String pageTitle;
    @Schema(description = "页面地址")
    private String pageUrl;
    @Schema(description = "页面内容摘要")
    private String contentHash;
    @Schema(description = "最后同步时间")
    private Date lastSyncTime;
    @Schema(description = "条目数量")
    private Integer itemCount;
    @Schema(description = "抓取原始HTML")
    private String rawHtml;
    @Schema(description = "条目列表")
    private List<TodayHistoryItemDto> items;
}
