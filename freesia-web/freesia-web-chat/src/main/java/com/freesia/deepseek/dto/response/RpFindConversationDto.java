package com.freesia.deepseek.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 查找对话响应 数据传输对象
 * @date 2026-07-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查找对话响应 数据传输对象")
public class RpFindConversationDto {
    @Schema(description = "对话列表")
    private List<Conversation> conversations;

    @Schema(description = "对话")
    @Data
    public static class Conversation {
        @Schema(description = "对话ID")
        private String conversationId;
        @Schema(description = "对话标题")
        private String title;
        @Schema(description = "对话模型")
        private String chatMode;
    }
}
