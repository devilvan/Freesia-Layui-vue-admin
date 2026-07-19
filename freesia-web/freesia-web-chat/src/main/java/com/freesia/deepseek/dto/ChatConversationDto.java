package com.freesia.deepseek.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 交互式会话 数据传输对象
 * @date 2026-07-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "交互式会话 数据传输对象")
public class ChatConversationDto extends BaseDto {
    @Schema(description = "服务商编码")
    private String providerCode;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "会话标题")
    private String title;
    @Schema(description = "对话模式(runtime/fde)")
    private String chatMode;
}
