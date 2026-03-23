package com.freesia.vo;

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
 * @Description 睡眠产品评论 值对象
 * @date 2026-03-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "睡眠产品评论 值对象")
public class SleepCommentHeaderVo extends BaseVo {
    @Schema(description = "来源")
    @JsonAlias(value = {"source"})
    private String source;
    @Schema(description = "评论人ID")
    @JsonAlias(value = {"userId"})
    private String userId;
    @Schema(description = "评论人名称")
    @JsonAlias(value = {"userName"})
    private String userName;
    @Schema(description = "标题")
    @JsonAlias(value = {"title"})
    private String title;
    @Schema(description = "评论内容")
    @JsonAlias(value = {"content"})
    private String content;
    @Schema(description = "评分")
    @JsonAlias(value = {"level"})
    private String level;
    @Schema(description = "发布时间")
    @JsonAlias(value = {"operateTime"})
    private Date operateTime;
    @Schema(description = "楼层")
    @JsonAlias(value = {"floor"})
    private String floor;
    @Schema(description = "内容类型")
    @JsonAlias(value = {"contentType"})
    private String contentType;
    @Schema(description = "URL")
    @JsonAlias(value = {"url"})
    private String url;
    @Schema(description = "评分")
    @JsonAlias(value = {"grade"})
    private Integer grade;
    @Schema(description = "评论数")
    @JsonAlias(value = {"commentNum"})
    private Integer commentNum;
}
