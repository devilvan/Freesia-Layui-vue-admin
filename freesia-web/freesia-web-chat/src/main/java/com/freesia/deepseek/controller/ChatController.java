package com.freesia.deepseek.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.freesia.deepseek.dto.ChatConversationDto;
import com.freesia.deepseek.dto.ChatMessageDto;
import com.freesia.deepseek.dto.response.RpFindConversationDto;
import com.freesia.deepseek.dto.response.RpFindHistoryDto;
import com.freesia.deepseek.po.ChatConversationPo;
import com.freesia.deepseek.po.ChatMessagePo;
import com.freesia.deepseek.repository.ChatConversationRepository;
import com.freesia.deepseek.repository.ChatMessageRepository;
import com.freesia.deepseek.service.ChatConversationService;
import com.freesia.deepseek.service.ChatMessageService;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.UEmpty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "对话会话管理 控制器")
public class ChatController {

    private final ChatConversationService conversationService;
    private final ChatMessageService messageService;
    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;

    @SaIgnore
    @GetMapping("/conversations")
    @Operation(summary = "获取对话列表")
    public RpFindConversationDto findConversations(@RequestParam(value = "chatMode", required = false) String chatMode) {
        Long userId = USecurity.getUserId();
        ChatConversationDto query = new ChatConversationDto();
        query.setUserId(userId);
        if (chatMode != null && !chatMode.isEmpty()) {
            query.setChatMode(chatMode);
        }
        List<ChatConversationDto> conversations = conversationService.findList(query);
        if (UEmpty.isEmpty(conversations)) {
            return new RpFindConversationDto(Collections.emptyList());
        }
        List<RpFindConversationDto.Conversation> list = conversations.stream().map(dto -> {
            RpFindConversationDto.Conversation item = new RpFindConversationDto.Conversation();
            item.setConversationId(String.valueOf(dto.getId()));
            item.setTitle(dto.getTitle() != null ? dto.getTitle() : "新对话");
            if (dto.getChatMode() != null) item.setChatMode(dto.getChatMode());
            return item;
        }).collect(Collectors.toList());
        RpFindConversationDto result = new RpFindConversationDto();
        result.setConversations(list);
        return result;
    }

    @SaIgnore
    @GetMapping("/{id}/history")
    @Operation(summary = "获取对话历史")
    public RpFindHistoryDto findHistory(@PathVariable String id) {
        ChatConversationPo conv = findConversation(id);
        if (conv == null) {
            RpFindHistoryDto result = new RpFindHistoryDto();
            result.setMessages(Collections.emptyList());
            return result;
        }
        List<ChatMessagePo> messages = messageRepository.findByConversationIdOrderByOrderNumAsc(conv.getId());
        if (UEmpty.isEmpty(messages)) {
            return new RpFindHistoryDto(Collections.emptyList());
        }
        List<RpFindHistoryDto.Message> msgList = messages.stream().map(msg -> {
            RpFindHistoryDto.Message item = new RpFindHistoryDto.Message();
            item.setId(String.valueOf(msg.getId()));
            item.setRole(msg.getRole());
            item.setContent(msg.getContent());
            if (msg.getOrderNum() != null) item.setOrderNum(msg.getOrderNum());
            return item;
        }).collect(Collectors.toList());
        RpFindHistoryDto result = new RpFindHistoryDto();
        result.setMessages(msgList);
        return result;
    }

    @SaIgnore
    @PutMapping("/{id}/history")
    @Operation(summary = "保存对话历史")
    @Transactional
    public Map<String, Object> saveHistory(@PathVariable String id, @RequestBody Map<String, Object> body) {
        ChatConversationPo conv = findConversation(id);
        Long userId = USecurity.getUserId();
        if (conv == null) {
            String title = (String) body.getOrDefault("title", "新对话");
            String chatMode = (String) body.get("chatMode");
            ChatConversationDto dto = new ChatConversationDto();
            dto.setUserId(userId);
            dto.setTitle(title);
            dto.setChatMode(chatMode);
            dto.setProviderCode("DeepSeek");
            ChatConversationDto saved = conversationService.saveUpdate(dto);
            try {
                Long.parseLong(id);
            } catch (NumberFormatException e) {
                ChatConversationPo po = conversationRepository.findById(saved.getId()).orElse(null);
                if (po != null) {
                    po.setExtId(id);
                    conversationRepository.save(po);
                }
            }
            conv = conversationRepository.findById(saved.getId()).orElse(null);
        } else {
            String title = (String) body.getOrDefault("title", null);
            if (title != null && !title.equals(conv.getTitle())) {
                ChatConversationDto dto = new ChatConversationDto();
                dto.setId(conv.getId());
                dto.setTitle(title);
                dto.setUserId(conv.getUserId());
                dto.setChatMode(conv.getChatMode());
                conversationService.saveUpdate(dto);
            }
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> messages = (List<Map<String, Object>>) body.get("messages");
        if (messages != null && conv != null) {
            messageRepository.deleteByConversationId(conv.getId());
            int orderNum = 0;
            for (Map<String, Object> msg : messages) {
                ChatMessageDto msgDto = new ChatMessageDto();
                msgDto.setConversationId(conv.getId());
                msgDto.setRole((String) msg.getOrDefault("role", "user"));
                msgDto.setContent((String) msg.getOrDefault("content", ""));
                msgDto.setOrderNum(orderNum++);
                messageService.saveUpdate(msgDto);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @SaIgnore
    @DeleteMapping("/{id}")
    @Operation(summary = "删除对话")
    @Transactional
    public Map<String, Object> deleteConversation(@PathVariable String id) {
        ChatConversationPo conv = findConversation(id);
        if (conv != null) {
            messageRepository.deleteByConversationId(conv.getId());
            conversationService.deleteBatch(Collections.singletonList(conv.getId()));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @SaIgnore
    @PatchMapping("/{id}/title")
    @Operation(summary = "重命名对话")
    public Map<String, Object> renameConversation(@PathVariable String id, @RequestBody Map<String, Object> body) {
        ChatConversationPo conv = findConversation(id);
        if (conv == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            return result;
        }
        String title = (String) body.get("title");
        ChatConversationDto dto = new ChatConversationDto();
        dto.setId(conv.getId());
        dto.setTitle(title);
        dto.setUserId(conv.getUserId());
        dto.setChatMode(conv.getChatMode());
        conversationService.saveUpdate(dto);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    private ChatConversationPo findConversation(String id) {
        ChatConversationPo conv = conversationRepository.findByExtId(id).orElse(null);
        if (conv != null) return conv;
        try {
            return conversationRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
