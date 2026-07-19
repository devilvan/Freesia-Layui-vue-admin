package com.freesia.deepseek.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.freesia.deepseek.entity.ChatConversation;
import com.freesia.deepseek.entity.ChatMessage;
import com.freesia.deepseek.service.ChatService;
import com.freesia.satoken.util.USecurity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "对话会话管理 控制器")
public class ChatController {

    private final ChatService chatService;

    @SaCheckLogin
    @GetMapping("/conversations")
    @Operation(summary = "获取对话列表")
    public Map<String, Object> getConversations(
            @RequestParam(value = "userId", required = false) String userIdParam,
            @RequestParam(value = "chatMode", required = false) String chatMode) {
        Long userId = USecurity.getUserId();
        List<ChatConversation> conversations = chatService.listConversations(userId, chatMode);

        List<Map<String, Object>> list = conversations.stream().map(conv -> {
            Map<String, Object> item = new HashMap<>();
            item.put("conversationId", conv.getId());
            item.put("title", conv.getTitle());
            if (conv.getChatMode() != null) {
                item.put("chatMode", conv.getChatMode());
            }
            item.put("createdAt", conv.getCreatedAt() != null ? conv.getCreatedAt().toString() : null);
            item.put("updatedAt", conv.getUpdatedAt() != null ? conv.getUpdatedAt().toString() : null);
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("conversations", list);
        return result;
    }

    @SaCheckLogin
    @GetMapping("/{id}/history")
    @Operation(summary = "获取对话历史")
    public Map<String, Object> getHistory(@PathVariable String id) {
        List<ChatMessage> messages = chatService.getMessages(id);

        List<Map<String, Object>> msgList = messages.stream().map(msg -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", msg.getId());
            item.put("role", msg.getRole());
            item.put("content", msg.getContent());
            item.put("timestamp", msg.getTimestamp());
            if (msg.getCards() != null) {
                item.put("cards", msg.getCards());
            }
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("messages", msgList);
        return result;
    }

    @SaCheckLogin
    @PutMapping("/{id}/history")
    @Operation(summary = "保存对话历史")
    @SuppressWarnings("unchecked")
    public Map<String, Object> saveHistory(@PathVariable String id, @RequestBody Map<String, Object> body) {
        String title = (String) body.getOrDefault("title", "新对话");
        List<Map<String, Object>> messages = (List<Map<String, Object>>) body.get("messages");

        ChatConversation existing = chatService.getConversation(id);
        if (existing == null) {
            Long userId = USecurity.getUserId();
            String chatMode = (String) body.get("chatMode");
            chatService.createConversation(id, userId, title, chatMode);
        }

        if (messages != null) {
            chatService.saveHistory(id, title, messages);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @SaCheckLogin
    @DeleteMapping("/{id}")
    @Operation(summary = "删除对话")
    public Map<String, Object> deleteConversation(@PathVariable String id) {
        chatService.deleteConversation(id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @SaCheckLogin
    @PatchMapping("/{id}/title")
    @Operation(summary = "重命名对话")
    public Map<String, Object> renameConversation(@PathVariable String id, @RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        boolean ok = chatService.renameConversation(id, title);

        Map<String, Object> result = new HashMap<>();
        result.put("success", ok);
        return result;
    }
}
