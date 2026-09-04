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
 * @Description 历史上的今天-条目表 值对象
 * @date 2026-09-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "历史上的今天-条目表 值对象")
public class TodayHistoryItemVo extends BaseVo {
    @Schema(description = "页面ID")
    @JsonAlias(value = {"pageId"})
    private Long pageId;
    @Schema(description = "历史日期键（MM-DD）")
    @JsonAlias(value = {"historyKey"})
    private String historyKey;
    @Schema(description = "条目类型")
    @JsonAlias(value = {"itemType"})
    private String itemType;
    @Schema(description = "时代类型")
    @JsonAlias(value = {"eraType"})
    private String eraType;
    @Schema(description = "分组标题")
    @JsonAlias(value = {"sectionTitle"})
    private String sectionTitle;
    @Schema(description = "年份")
    @JsonAlias(value = {"eventYear"})
    private Integer eventYear;
    @Schema(description = "排序号")
    @JsonAlias(value = {"sortNo"})
    private Integer sortNo;
    @Schema(description = "条目哈希")
    @JsonAlias(value = {"itemHash"})
    private String itemHash;
    @Schema(description = "条目内容")
    @JsonAlias(value = {"content"})
    private String content;
}
