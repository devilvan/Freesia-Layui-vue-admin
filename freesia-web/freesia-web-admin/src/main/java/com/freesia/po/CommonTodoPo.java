package com.freesia.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 待办事项表 映射
 * @date 2026-01-04
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "COMMON_TODO")

@Entity
@Table(name = "COMMON_TODO")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "待办事项表 映射")
public class CommonTodoPo extends BasePo implements Serializable {
    @Schema(description = "用户ID")
    @TableField(value = "USER_ID")
    @Column(name = "USER_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '用户ID'")
    private Long userId;
    @Schema(description = "标题")
    @TableField(value = "TITLE")
    @Column(name = "TITLE", columnDefinition = "VARCHAR(128) COMMENT '标题'")
    private String title;
    @Schema(description = "内容")
    @TableField(value = "CONTENT")
    @Column(name = "CONTENT", columnDefinition = "TEXT(65,535) NOT NULL COMMENT '内容'")
    private String content;
    @Schema(description = "状态（0-未完成；1-已完成）")
    @TableField(value = "STATUS")
    @Column(name = "STATUS", columnDefinition = "BIT(1) COMMENT '状态（0-未完成；1-已完成）'")
    private Boolean status;
    @Schema(description = "提醒时间")
    @TableField(value = "DUE_TIME")
    @Column(name = "DUE_TIME", columnDefinition = "DATETIME COMMENT '提醒时间'")
    private Date dueTime;
    @Schema(description = "发送提醒标识（0-否；1-是）")
    @TableField(value = "REMINDER_SEND_FLAG")
    @Column(name = "REMINDER_SEND_FLAG", columnDefinition = "TINYINT(1) COMMENT '发送提醒标识（0-否；1-是）'")
    private Boolean reminderSendFlag;
    @Schema(description = "优先级（0-高；1-中；2-低）")
    @TableField(value = "PRIORITY")
    @Column(name = "PRIORITY", columnDefinition = "INT(10) COMMENT '优先级（0-高；1-中；2-低）'")
    private Integer priority;
}
