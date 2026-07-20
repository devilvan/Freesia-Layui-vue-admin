package com.freesia.deepseek.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 保存对话历史 数据传输对象
 * @date 2026-07-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RqSaveHistoryDto {
    @Schema(description = "对话标题")
    private String title;
    @Schema(description = "对话模式")
    private String chatMode;
    @Schema(description = "对话内容")
    private List<Message> messages;

    @Data
    public static class Message {
        @Schema(description = "消息角色")
        private String role;
        @Schema(description = "消息内容")
        private String content;
    }

}
