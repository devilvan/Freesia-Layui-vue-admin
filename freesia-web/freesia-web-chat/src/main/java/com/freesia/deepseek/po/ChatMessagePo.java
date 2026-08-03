package com.freesia.deepseek.po;

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

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 交互式会话-消息 映射
 * @date 2026-07-19
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "CHAT_MESSAGE")

@Entity
@Table(name = "CHAT_MESSAGE")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "交互式会话-消息 映射")
public class ChatMessagePo extends BasePo implements Serializable {
    @Schema(description = "会话ID")
    @TableField(value = "CONVERSATION_ID")
    @Column(name = "CONVERSATION_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '会话ID'")
    private Long conversationId;
    @Schema(description = "角色(user/assistant/status)")
    @TableField(value = "ROLE")
    @Column(name = "ROLE", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '角色(user/assistant/status)'")
    private String role;
    @Schema(description = "消息内容")
    @TableField(value = "CONTENT")
    @Column(name = "CONTENT", columnDefinition = "LONGTEXT COMMENT '消息内容'")
    private String content;
    @Schema(description = "消息排序")
    @TableField(value = "ORDER_NUM")
    @Column(name = "ORDER_NUM", columnDefinition = "INT(10) COMMENT '消息排序'")
    private Integer orderNum;
}
