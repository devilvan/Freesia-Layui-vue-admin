package com.freesia.deepseek.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 交互式会话-消息 数据传输对象
 * @date 2026-07-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "交互式会话-消息 数据传输对象")
public class ChatMessageDto extends BaseDto {
    @Schema(description = "会话ID")
    private Long conversationId;
    @Schema(description = "角色(user/assistant/status)")
    private String role;
    @Schema(description = "消息内容")
    private String content;
    @Schema(description = "消息排序")
    private Integer orderNum;
}
