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
 * @Description 历史上的今天-页面表 映射
 * @date 2026-09-04
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "TODAY_HISTORY_PAGE")

@Entity
@Table(name = "TODAY_HISTORY_PAGE", indexes = {
    @Index(name = "uk_today_history_page_key", columnList = "HISTORY_KEY", unique = true),
    @Index(name = "idx_today_history_page_month_day", columnList = "MONTH_VALUE, DAY_VALUE")
})
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "历史上的今天-页面表 映射")
public class TodayHistoryPagePo extends BasePo implements Serializable {
    @Schema(description = "月份")
    @TableField(value = "MONTH_VALUE")
    @Column(name = "MONTH_VALUE", columnDefinition = "INT(10) NOT NULL COMMENT '月份'")
    private Integer monthValue;
    @Schema(description = "日期")
    @TableField(value = "DAY_VALUE")
    @Column(name = "DAY_VALUE", columnDefinition = "INT(10) NOT NULL COMMENT '日期'")
    private Integer dayValue;
    @Schema(description = "历史日期键（MM-DD）")
    @TableField(value = "HISTORY_KEY")
    @Column(name = "HISTORY_KEY", columnDefinition = "VARCHAR(10) NOT NULL COMMENT '历史日期键（MM-DD）'")
    private String historyKey;
    @Schema(description = "页面标题")
    @TableField(value = "PAGE_TITLE")
    @Column(name = "PAGE_TITLE", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '页面标题'")
    private String pageTitle;
    @Schema(description = "页面地址")
    @TableField(value = "PAGE_URL")
    @Column(name = "PAGE_URL", columnDefinition = "VARCHAR(500) NOT NULL COMMENT '页面地址'")
    private String pageUrl;
    @Schema(description = "页面内容摘要")
    @TableField(value = "CONTENT_HASH")
    @Column(name = "CONTENT_HASH", columnDefinition = "VARCHAR(64) COMMENT '页面内容摘要'")
    private String contentHash;
    @Schema(description = "最后同步时间")
    @TableField(value = "LAST_SYNC_TIME")
    @Column(name = "LAST_SYNC_TIME", columnDefinition = "DATETIME COMMENT '最后同步时间'")
    private Date lastSyncTime;
    @Schema(description = "条目数量")
    @TableField(value = "ITEM_COUNT")
    @Column(name = "ITEM_COUNT", columnDefinition = "INT(10) COMMENT '条目数量'")
    private Integer itemCount;
    @Schema(description = "抓取原始HTML")
    @TableField(value = "RAW_HTML")
    @Lob
    @Column(name = "RAW_HTML", columnDefinition = "LONGTEXT COMMENT '抓取原始HTML'")
    private String rawHtml;
}
