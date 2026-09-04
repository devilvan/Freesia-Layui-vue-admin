package com.freesia.todayhistory.vo;

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
 * @Description 历史上的今天-页面表 值对象
 * @date 2026-09-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "历史上的今天-页面表 值对象")
public class TodayHistoryPageVo extends BaseVo {
    @Schema(description = "月份")
    @JsonAlias(value = {"monthValue"})
    private Integer monthValue;
    @Schema(description = "日期")
    @JsonAlias(value = {"dayValue"})
    private Integer dayValue;
    @Schema(description = "历史日期键（MM-DD）")
    @JsonAlias(value = {"historyKey"})
    private String historyKey;
    @Schema(description = "页面标题")
    @JsonAlias(value = {"pageTitle"})
    private String pageTitle;
    @Schema(description = "页面地址")
    @JsonAlias(value = {"pageUrl"})
    private String pageUrl;
    @Schema(description = "页面内容摘要")
    @JsonAlias(value = {"contentHash"})
    private String contentHash;
    @Schema(description = "最后同步时间")
    @JsonAlias(value = {"lastSyncTime"})
    private Date lastSyncTime;
    @Schema(description = "条目数量")
    @JsonAlias(value = {"itemCount"})
    private Integer itemCount;
    @Schema(description = "抓取原始HTML")
    @JsonAlias(value = {"rawHtml"})
    private String rawHtml;
}
