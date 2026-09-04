package com.freesia.todayhistory.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 历史上的今天-链接表 数据传输对象
 * @date 2026-09-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "历史上的今天-链接表 数据传输对象")
public class TodayHistoryLinkDto extends BaseDto {
    @Schema(description = "页面ID")
    private Long pageId;
    @Schema(description = "条目ID")
    private Long itemId;
    @Schema(description = "历史日期键（MM-DD）")
    private String historyKey;
    @Schema(description = "链接文本")
    private String linkText;
    @Schema(description = "链接地址")
    private String linkUrl;
    @Schema(description = "链接标题")
    private String linkTitle;
    @Schema(description = "是否站内链接")
    private Boolean internalFlag;
    @Schema(description = "排序号")
    private Integer sortNo;
    @Schema(description = "链接哈希")
    private String linkHash;
}
