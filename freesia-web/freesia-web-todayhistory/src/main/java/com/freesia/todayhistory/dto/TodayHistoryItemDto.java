package com.freesia.todayhistory.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 历史上的今天-条目表 数据传输对象
 * @date 2026-09-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "历史上的今天-条目表 数据传输对象")
public class TodayHistoryItemDto extends BaseDto {
    @Schema(description = "页面ID")
    private Long pageId;
    @Schema(description = "历史日期键（MM-DD）")
    private String historyKey;
    @Schema(description = "条目类型")
    private String itemType;
    @Schema(description = "时代类型")
    private String eraType;
    @Schema(description = "分组标题")
    private String sectionTitle;
    @Schema(description = "年份")
    private Integer eventYear;
    @Schema(description = "排序号")
    private Integer sortNo;
    @Schema(description = "条目哈希")
    private String itemHash;
    @Schema(description = "条目内容")
    private String content;
    @Schema(description = "条目链接")
    private List<TodayHistoryLinkDto> links;

}
