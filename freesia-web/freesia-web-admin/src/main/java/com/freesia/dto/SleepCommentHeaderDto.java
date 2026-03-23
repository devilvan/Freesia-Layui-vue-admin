package com.freesia.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 睡眠产品评论 数据传输对象
 * @date 2026-03-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "睡眠产品评论 数据传输对象")
public class SleepCommentHeaderDto extends BaseDto {
    @Schema(description = "来源")
    private String source;
    @Schema(description = "评论人ID")
    private String userId;
    @Schema(description = "评论人名称")
    private String userName;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "评论内容")
    private String content;
    @Schema(description = "评分")
    private String level;
    @Schema(description = "发布时间")
    private Date operateTime;
    @Schema(description = "楼层")
    private String floor;
    @Schema(description = "内容类型")
    private String contentType;
    @Schema(description = "URL")
    private String url;
    @Schema(description = "评分")
    private Integer grade;
    @Schema(description = "评论数")
    private Integer commentNum;
}
