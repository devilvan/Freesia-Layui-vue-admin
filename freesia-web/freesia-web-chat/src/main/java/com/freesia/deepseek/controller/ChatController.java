package com.freesia.deepseek.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.freesia.constant.Constants;
import com.freesia.controller.BaseController;
import com.freesia.deepseek.dto.ChatConversationDto;
import com.freesia.deepseek.dto.ChatMessageDto;
import com.freesia.deepseek.dto.ChatRequestCommand;
import com.freesia.deepseek.dto.request.RqSaveHistoryDto;
import com.freesia.deepseek.dto.response.RpFindConversationDto;
import com.freesia.deepseek.dto.response.RpFindHistoryDto;
import com.freesia.deepseek.po.ChatConversationPo;
import com.freesia.deepseek.po.ChatMessagePo;
import com.freesia.deepseek.repository.ChatConversationRepository;
import com.freesia.deepseek.repository.ChatMessageRepository;
import com.freesia.deepseek.service.ChatConversationService;
import com.freesia.deepseek.service.ChatMessageService;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.UCollection;
import com.freesia.util.UEmpty;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Bliss.Wu
 * @Description 聊天会话接口
 * @date 2026-07-19
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "聊天会话接口")
public class ChatController extends BaseController {
    private final DeepSeekClient deepSeekClient;
    private final ChatConversationService chatConversationService;
    private final ChatMessageService chatMessageService;
    private final ChatConversationRepository chatConversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final TransactionTemplate transactionTemplate;

    @SaIgnore
    @Operation(summary = "聊天流")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatCompletionResponse> chatStream(@RequestBody ChatRequestCommand command) {
        ChatCompletionRequest.Builder builder = ChatCompletionRequest.builder()
                .model("deepseek-chat")
                .stream(true);

        if (command.getMessages() != null) {
            for (ChatRequestCommand.Message msg : command.getMessages()) {
                if (msg.getRole() == null || msg.getContent() == null) {
                    continue;
                }
                Constants.Role role = Constants.Role.getInstanceByCode(msg.getRole());
                switch (role) {
                    case SYSTEM:
                        builder.addSystemMessage(msg.getContent());
                        break;
                    case ASSISTANT:
                        builder.addAssistantMessage(msg.getContent());
                        break;
                    case USER:
                        builder.addUserMessage(msg.getContent());
                    default:
                        break;
                }
            }
        }

        return deepSeekClient.chatFluxCompletion(builder.build());
    }

    @SaIgnore
    @GetMapping("/conversations")
    @Operation(summary = "获取聊天会话列表")
    public RpFindConversationDto findConversations(@RequestParam(value = "chatMode", required = false) String chatMode) {
        Long userId = USecurity.getUserId();
        ChatConversationDto query = new ChatConversationDto();
        query.setUserId(userId);
        if (chatMode != null && !chatMode.isEmpty()) {
            query.setChatMode(chatMode);
        }
        List<ChatConversationDto> conversations = chatConversationService.findList(query);
        if (UEmpty.isEmpty(conversations)) {
            return new RpFindConversationDto(Collections.emptyList());
        }
        List<RpFindConversationDto.Conversation> list = conversations.stream().map(dto -> {
            RpFindConversationDto.Conversation item = new RpFindConversationDto.Conversation();
            item.setConversationId(String.valueOf(dto.getId()));
            item.setTitle(dto.getTitle() != null ? dto.getTitle() : "新会话");
            if (UEmpty.isEmpty(dto.getChatMode())) {
                item.setChatMode(dto.getChatMode());
            }
            return item;
        }).collect(Collectors.toList());
        RpFindConversationDto result = new RpFindConversationDto();
        result.setConversations(list);
        return result;
    }

    @SaIgnore
    @GetMapping("/{id}/history")
    @Operation(summary = "获取聊天记录历史")
    public RpFindHistoryDto findHistory(@PathVariable String id) {
        ChatConversationPo conv = findConversation(id);
        if (conv == null) {
            RpFindHistoryDto result = new RpFindHistoryDto();
            result.setMessages(Collections.emptyList());
            return result;
        }
        List<ChatMessagePo> messages = chatMessageRepository.findByConversationIdOrderByOrderNumAsc(conv.getId());
        if (UEmpty.isEmpty(messages)) {
            return new RpFindHistoryDto(Collections.emptyList());
        }
        List<RpFindHistoryDto.Message> msgList = messages.stream().map(msg -> {
            RpFindHistoryDto.Message item = new RpFindHistoryDto.Message();
            item.setId(String.valueOf(msg.getId()));
            item.setRole(msg.getRole());
            item.setContent(msg.getContent());
            if (msg.getOrderNum() != null) {
                item.setOrderNum(msg.getOrderNum());
            }
            return item;
        }).collect(Collectors.toList());
        RpFindHistoryDto result = new RpFindHistoryDto();
        result.setMessages(msgList);
        return result;
    }

    @SaIgnore
    @PutMapping("/{id}/history")
    @Operation(summary = "保存聊天记录历史")
    public Map<String, Object> saveHistory(@PathVariable String id, @RequestBody RqSaveHistoryDto rqSaveHistoryDto) {
        ChatConversationPo conv = findConversation(id);
        if (conv == null) {
            String title = rqSaveHistoryDto.getTitle();
            String chatMode = rqSaveHistoryDto.getChatMode();
            ChatConversationDto dto = new ChatConversationDto();
            dto.setUserId(USecurity.getUserId());
            dto.setTitle(title);
            dto.setChatMode(chatMode);
            dto.setProviderCode(Constants.Provider.DEEPSEEK.getCode());
            ChatConversationDto saved = chatConversationService.saveUpdate(dto);
            try {
                Long.parseLong(id);
            } catch (NumberFormatException e) {
                ChatConversationPo po = chatConversationRepository.findById(saved.getId()).orElse(null);
                if (po != null) {
                    po.setExtId(id);
                    conv = chatConversationRepository.save(po);
                }
            }
        } else {
            String title = rqSaveHistoryDto.getTitle();
            if (title != null && !title.equals(conv.getTitle())) {
                ChatConversationDto dto = new ChatConversationDto();
                dto.setId(conv.getId());
                dto.setTitle(title);
                dto.setUserId(conv.getUserId());
                dto.setChatMode(conv.getChatMode());
                chatConversationService.saveUpdate(dto);
            }
        }
        List<RqSaveHistoryDto.Message> messages = rqSaveHistoryDto.getMessages();
        if (messages != null && conv != null) {
            Long covId = conv.getId();
            transactionTemplate.execute(status -> {
                chatMessageRepository.deleteByConversationId(covId);
                int orderNum = 0;
                for (RqSaveHistoryDto.Message msg : messages) {
                    ChatMessageDto msgDto = new ChatMessageDto();
                    msgDto.setConversationId(covId);
                    msgDto.setRole(msg.getRole());
                    msgDto.setContent(msg.getContent());
                    msgDto.setOrderNum(orderNum++);
                    chatMessageService.saveUpdate(msgDto);
                }
                return null;
            });
        }
        Map<String, Object> result = UCollection.optimizeInitialCapacityMap();
        result.put("success", true);
        return result;
    }

    @SaIgnore
    @DeleteMapping("/{id}")
    @Operation(summary = "删除聊天会话")
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteConversation(@PathVariable String id) {
        ChatConversationPo conv = findConversation(id);
        if (conv != null) {
            chatMessageRepository.deleteByConversationId(conv.getId());
            chatConversationService.deleteBatch(Collections.singletonList(conv.getId()));
        }
        Map<String, Object> result = UCollection.optimizeInitialCapacityMap();
        result.put("success", true);
        return result;
    }

    @PatchMapping("/{id}/title")
    @Operation(summary = "重命名聊天会话")
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> renameConversation(@PathVariable String id, @RequestBody Map<String, Object> body) {
        ChatConversationPo conv = findConversation(id);
        if (conv == null) {
            Map<String, Object> result = UCollection.optimizeInitialCapacityMap();
            result.put("success", false);
            return result;
        }
        String title = (String) body.get("title");
        ChatConversationDto dto = new ChatConversationDto();
        dto.setId(conv.getId());
        dto.setTitle(title);
        dto.setUserId(conv.getUserId());
        dto.setChatMode(conv.getChatMode());
        chatConversationService.saveUpdate(dto);
        Map<String, Object> result = UCollection.optimizeInitialCapacityMap();
        result.put("success", true);
        return result;
    }

    private ChatConversationPo findConversation(String id) {
        ChatConversationPo conv = chatConversationRepository.findByExtId(id).orElse(null);
        if (conv != null) {
            return conv;
        }
        try {
            return chatConversationRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
