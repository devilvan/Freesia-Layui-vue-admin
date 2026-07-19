package com.freesia.deepseek.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 查找历史消息 数据传输对象
 * @date 2026-07-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查找历史消息 数据传输对象")
public class RpFindHistoryDto {
    @Schema(description = "消息列表")
    private List<Message> messages;

    @Schema(description = "消息")
    @Data
    public static class Message {
        @Schema(description = "消息ID")
        private String id;
        @Schema(description = "消息角色(user/assistant/status)")
        private String role;
        @Schema(description = "消息内容")
        private String content;
        @Schema(description = "消息排序")
        private Integer orderNum;
    }
}
