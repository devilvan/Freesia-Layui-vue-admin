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

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 交互式会话 映射
 * @date 2026-07-19
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "CHAT_CONVERSATION")

@Entity
@Table(name = "CHAT_CONVERSATION")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "交互式会话 映射")
public class ChatConversationPo extends BasePo implements Serializable {
    @Schema(description = "服务商编码")
    @TableField(value = "PROVIDER_CODE")
    @Column(name = "PROVIDER_CODE", columnDefinition = "VARCHAR(32) COMMENT '服务商编码'")
    private String providerCode;
    @Schema(description = "用户ID")
    @TableField(value = "USER_ID")
    @Column(name = "USER_ID", columnDefinition = "BIGINT(19) COMMENT '用户ID'")
    private Long userId;
    @Schema(description = "会话标题")
    @TableField(value = "TITLE")
    @Column(name = "TITLE", columnDefinition = "VARCHAR(200) COMMENT '会话标题'")
    private String title;
    @Schema(description = "对话模式(runtime/fde)")
    @TableField(value = "CHAT_MODE")
    @Column(name = "CHAT_MODE", columnDefinition = "VARCHAR(32) COMMENT '对话模式(runtime/fde)'")
    private String chatMode;
}
