package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freesia.po.BasePo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 睡眠产品评论 映射
 * @date 2026-03-23
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SLEEP_COMMENT_HEADER")

@Entity
@Table(name = "SLEEP_COMMENT_HEADER")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "睡眠产品评论 映射")
public class SleepCommentHeaderPo extends BasePo implements Serializable {
    @Schema(description = "来源")
    @TableField(value = "SOURCE")
    @Column(name = "SOURCE", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '来源'")
    private String source;
    @Schema(description = "评论人ID")
    @TableField(value = "USER_ID")
    @Column(name = "USER_ID", columnDefinition = "VARCHAR(128) COMMENT '评论人ID'")
    private String userId;
    @Schema(description = "评论人名称")
    @TableField(value = "USER_NAME")
    @Column(name = "USER_NAME", columnDefinition = "VARCHAR(128) COMMENT '评论人名称'")
    private String userName;
    @Schema(description = "标题")
    @TableField(value = "TITLE")
    @Column(name = "TITLE", columnDefinition = "VARCHAR(128) COMMENT '标题'")
    private String title;
    @Schema(description = "评论内容")
    @TableField(value = "CONTENT")
    @Column(name = "CONTENT", columnDefinition = "TEXT(65,535) COMMENT '评论内容'")
    private String content;
    @Schema(description = "评分")
    @TableField(value = "LEVEL")
    @Column(name = "LEVEL", columnDefinition = "VARCHAR(16) COMMENT '评分'")
    private String level;
    @Schema(description = "发布时间")
    @TableField(value = "OPERATE_TIME")
    @Column(name = "OPERATE_TIME", columnDefinition = "DATETIME COMMENT '发布时间'")
    private Date operateTime;
    @Schema(description = "楼层")
    @TableField(value = "FLOOR")
    @Column(name = "FLOOR", columnDefinition = "VARCHAR(16) COMMENT '楼层'")
    private String floor;
    @Schema(description = "内容类型")
    @TableField(value = "CONTENT_TYPE")
    @Column(name = "CONTENT_TYPE", columnDefinition = "VARCHAR(16) COMMENT '内容类型'")
    private String contentType;
    @Schema(description = "URL")
    @TableField(value = "URL")
    @Column(name = "URL", columnDefinition = "TEXT(65,535) COMMENT 'URL'")
    private String url;
    @Schema(description = "分页数")
    @TableField(value = "PAGE")
    @Column(name = "PAGE", columnDefinition = "INT(10) COMMENT '分页数'")
    private Integer page;
    @Schema(description = "评论数")
    @TableField(value = "COMMENT_NUM")
    @Column(name = "COMMENT_NUM", columnDefinition = "INT(10) COMMENT '评论数'")
    private Integer commentNum;
    @Schema(description = "上级ID")
    @TableField(value = "PARENT_ID")
    @Column(name = "PARENT_ID", columnDefinition = "VARCHAR(64) COMMENT '上级ID'")
    private String parentId;
    @Schema(description = "UUID")
    @TableField(value = "UUID")
    @Column(name = "UUID", columnDefinition = "VARCHAR(64) COMMENT 'UUID'")
    private String uuid;
}
