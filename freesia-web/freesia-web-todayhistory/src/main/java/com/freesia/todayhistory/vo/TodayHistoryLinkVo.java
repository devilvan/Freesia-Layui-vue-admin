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
 * @Description 历史上的今天-链接表 值对象
 * @date 2026-09-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "历史上的今天-链接表 值对象")
public class TodayHistoryLinkVo extends BaseVo {
    @Schema(description = "页面ID")
    @JsonAlias(value = {"pageId"})
    private Long pageId;
    @Schema(description = "条目ID")
    @JsonAlias(value = {"itemId"})
    private Long itemId;
    @Schema(description = "历史日期键（MM-DD）")
    @JsonAlias(value = {"historyKey"})
    private String historyKey;
    @Schema(description = "链接文本")
    @JsonAlias(value = {"linkText"})
    private String linkText;
    @Schema(description = "链接地址")
    @JsonAlias(value = {"linkUrl"})
    private String linkUrl;
    @Schema(description = "链接标题")
    @JsonAlias(value = {"linkTitle"})
    private String linkTitle;
    @Schema(description = "是否站内链接")
    @JsonAlias(value = {"internalFlag"})
    private String internalFlag;
    @Schema(description = "排序号")
    @JsonAlias(value = {"sortNo"})
    private Integer sortNo;
    @Schema(description = "链接哈希")
    @JsonAlias(value = {"linkHash"})
    private String linkHash;
}
