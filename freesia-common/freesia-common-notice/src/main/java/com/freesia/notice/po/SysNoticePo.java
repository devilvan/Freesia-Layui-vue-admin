package com.freesia.notice.po;

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 消息公告表 映射
 * @date 2025-06-06
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "SYS_NOTICE")

@Entity
@Table(name = "SYS_NOTICE")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "消息公告表 映射")
public class SysNoticePo extends BasePo implements Serializable {
    @Schema(description = "标题")
    @TableField(value = "TITLE")
    @Column(name = "TITLE", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '标题'")
    private String title;
    @Schema(description = "通知类型（SYS_NOTICE_TYPE）")
    @TableField(value = "TYPE")
    @Column(name = "TYPE", columnDefinition = "VARCHAR(16) NOT NULL COMMENT '通知类型（SYS_NOTICE_TYPE）'")
    private String type;
    @Schema(description = "生效时间从")
    @TableField(value = "EFFECTIVE_TIME_FROM")
    @Column(name = "EFFECTIVE_TIME_FROM", columnDefinition = "DATETIME COMMENT '生效时间从'")
    private Date effectiveTimeFrom;
    @Schema(description = "生效时间到")
    @TableField(value = "EFFECTIVE_TIME_TO")
    @Column(name = "EFFECTIVE_TIME_TO", columnDefinition = "DATETIME COMMENT '生效时间到'")
    private Date effectiveTimeTo;
    @Schema(description = "内容")
    @TableField(value = "CONTENT")
    @Column(name = "CONTENT", columnDefinition = "TEXT(65535) NOT NULL COMMENT '内容'")
    private String content;
    @Schema(description = "发布人ID")
    @TableField(value = "PUBLISHER_ID")
    @Column(name = "PUBLISHER_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '发布人ID'")
    private Long publisherId;
    @Schema(description = "备注")
    @TableField(value = "REMARK")
    @Column(name = "REMARK", columnDefinition = "VARCHAR(128) COMMENT '备注'")
    private String remark;
}
