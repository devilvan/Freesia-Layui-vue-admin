package com.freesia.todayhistory.po;

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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 历史上的今天-条目表 映射
 * @date 2026-09-04
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "TODAY_HISTORY_ITEM")

@Entity
@Table(name = "TODAY_HISTORY_ITEM", indexes = {
    @Index(name = "uk_today_history_item_hash", columnList = "PAGE_ID, ITEM_HASH", unique = true),
    @Index(name = "idx_today_history_item_history_key", columnList = "HISTORY_KEY"),
    @Index(name = "idx_today_history_item_page_id", columnList = "PAGE_ID"),
    @Index(name = "idx_today_history_item_type", columnList = "ITEM_TYPE")
})
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "历史上的今天-条目表 映射")
public class TodayHistoryItemPo extends BasePo implements Serializable {
    @Schema(description = "页面ID")
    @TableField(value = "PAGE_ID")
    @Column(name = "PAGE_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '页面ID'")
    private Long pageId;
    @Schema(description = "历史日期键（MM-DD）")
    @TableField(value = "HISTORY_KEY")
    @Column(name = "HISTORY_KEY", columnDefinition = "VARCHAR(10) NOT NULL COMMENT '历史日期键（MM-DD）'")
    private String historyKey;
    @Schema(description = "条目类型")
    @TableField(value = "ITEM_TYPE")
    @Column(name = "ITEM_TYPE", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '条目类型'")
    private String itemType;
    @Schema(description = "时代类型")
    @TableField(value = "ERA_TYPE")
    @Column(name = "ERA_TYPE", columnDefinition = "VARCHAR(20) COMMENT '时代类型'")
    private String eraType;
    @Schema(description = "分组标题")
    @TableField(value = "SECTION_TITLE")
    @Column(name = "SECTION_TITLE", columnDefinition = "VARCHAR(64) COMMENT '分组标题'")
    private String sectionTitle;
    @Schema(description = "年份")
    @TableField(value = "EVENT_YEAR")
    @Column(name = "EVENT_YEAR", columnDefinition = "INT(10) COMMENT '年份'")
    private Integer eventYear;
    @Schema(description = "排序号")
    @TableField(value = "SORT_NO")
    @Column(name = "SORT_NO", columnDefinition = "INT(10) COMMENT '排序号'")
    private Integer sortNo;
    @Schema(description = "条目哈希")
    @TableField(value = "ITEM_HASH")
    @Column(name = "ITEM_HASH", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '条目哈希'")
    private String itemHash;
    @Schema(description = "条目内容")
    @TableField(value = "CONTENT")
    @Lob
    @Column(name = "CONTENT", columnDefinition = "LONGTEXT COMMENT '条目内容'")
    private String content;
}
