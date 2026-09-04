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
 * @Description 历史上的今天-链接表 映射
 * @date 2026-09-04
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "TODAY_HISTORY_LINK")

@Entity
@Table(name = "TODAY_HISTORY_LINK", indexes = {
    @Index(name = "uk_today_history_link_hash", columnList = "ITEM_ID, LINK_HASH", unique = true),
    @Index(name = "idx_today_history_link_history_key", columnList = "HISTORY_KEY"),
    @Index(name = "idx_today_history_link_item_id", columnList = "ITEM_ID"),
    @Index(name = "idx_today_history_link_page_id", columnList = "PAGE_ID")
})
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "历史上的今天-链接表 映射")
public class TodayHistoryLinkPo extends BasePo implements Serializable {
    @Schema(description = "页面ID")
    @TableField(value = "PAGE_ID")
    @Column(name = "PAGE_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '页面ID'")
    private Long pageId;
    @Schema(description = "条目ID")
    @TableField(value = "ITEM_ID")
    @Column(name = "ITEM_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '条目ID'")
    private Long itemId;
    @Schema(description = "历史日期键（MM-DD）")
    @TableField(value = "HISTORY_KEY")
    @Column(name = "HISTORY_KEY", columnDefinition = "VARCHAR(10) NOT NULL COMMENT '历史日期键（MM-DD）'")
    private String historyKey;
    @Schema(description = "链接文本")
    @TableField(value = "LINK_TEXT")
    @Column(name = "LINK_TEXT", columnDefinition = "VARCHAR(255) COMMENT '链接文本'")
    private String linkText;
    @Schema(description = "链接地址")
    @TableField(value = "LINK_URL")
    @Column(name = "LINK_URL", columnDefinition = "VARCHAR(1000) COMMENT '链接地址'")
    private String linkUrl;
    @Schema(description = "链接标题")
    @TableField(value = "LINK_TITLE")
    @Column(name = "LINK_TITLE", columnDefinition = "VARCHAR(255) COMMENT '链接标题'")
    private String linkTitle;
    @Schema(description = "是否站内链接")
    @TableField(value = "INTERNAL_FLAG")
    @Column(name = "INTERNAL_FLAG", columnDefinition = "BIT(1) COMMENT '是否站内链接'")
    private Boolean internalFlag;
    @Schema(description = "排序号")
    @TableField(value = "SORT_NO")
    @Column(name = "SORT_NO", columnDefinition = "INT(10) COMMENT '排序号'")
    private Integer sortNo;
    @Schema(description = "链接哈希")
    @TableField(value = "LINK_HASH")
    @Column(name = "LINK_HASH", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '链接哈希'")
    private String linkHash;
}
