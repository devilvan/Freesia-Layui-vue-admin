package com.freesia.deepseek.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.excel.EasyExcel;
import com.freesia.constant.Constants;
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
import com.freesia.rag.service.RagService;
import com.freesia.satoken.util.USecurity;
import com.freesia.sse.dto.SseEmitterUTF8;
import com.freesia.util.UCollection;
import com.freesia.util.UEmpty;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description Deepseek交互式对话 控制类
 * @date 2026-07-19
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "DeepseekChatController", description = "Deepseek交互式对话 控制器")
public class DeepseekChatController {
    private final DeepSeekClient deepSeekClient;
    private final ChatConversationService chatConversationService;
    private final ChatMessageService chatMessageService;
    private final ChatConversationRepository chatConversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final TransactionTemplate transactionTemplate;
    private final RagService ragService;
    /**
     * RAG 相似度阈值（0~1）：低于该相似度的检索片段视为无关，不注入对话上下文
     */
    @Value("${freesia.rag.similarity-threshold:0.5}")
    private double ragSimilarityThreshold;

    @SaIgnore
    @Operation(summary = "聊天流")
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitterUTF8 chatStream(@RequestBody ChatRequestCommand command) {
        ChatCompletionRequest.Builder builder = ChatCompletionRequest.builder()
                .model("deepseek-chat")
                .stream(true);

        // RAG：用最近一次用户提问在知识库中检索相关内容，作为系统上下文注入
        injectKnowledgeContext(builder, command);

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
                    default:
                        builder.addUserMessage(msg.getContent());
                        break;
                }
            }
        }

        SseEmitterUTF8 emitter = new SseEmitterUTF8(0L);
        Flux<ChatCompletionResponse> flux = deepSeekClient.chatFluxCompletion(builder.build());

        flux.subscribe(
                r -> {
                    try {
                        emitter.send(r);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                },
                emitter::completeWithError,
                emitter::complete
        );

        return emitter;
    }

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

    @SaIgnore
    @Operation(summary = "上传文件并解析内容")
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = UCollection.optimizeInitialCapacityMap();
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("error", "文件为空");
            return result;
        }

        String fileName = file.getOriginalFilename();
        String mimeType = file.getContentType();

        try {
            String content = parseFile(file, fileName, mimeType);
            result.put("success", true);
            result.put("content", content);
            result.put("fileName", fileName != null ? fileName : "unknown");
        } catch (Exception e) {
            log.error("文件解析失败: {}", fileName, e);
            result.put("success", false);
            result.put("error", "文件解析失败: " + e.getMessage());
        }
        return result;
    }

    private String parseFile(MultipartFile file, String fileName, String mimeType) throws Exception {
        if (fileName == null) fileName = "";

        String lowerName = fileName.toLowerCase();

        // Excel 文件
        if (lowerName.endsWith(".xlsx") || lowerName.endsWith(".xls")) {
            return parseExcel(file);
        }

        // CSV 文件
        if (lowerName.endsWith(".csv")) {
            return parseCsv(file);
        }

        // 纯文本文件
        if (mimeType != null && (mimeType.startsWith("text/") ||
                mimeType.contains("json") ||
                mimeType.contains("javascript") ||
                mimeType.contains("xml") ||
                mimeType.contains("yaml"))) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }

        // 默认尝试以文本读取
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("不支持的文件类型: " + (mimeType != null ? mimeType : "未知"));
        }
    }

    private String parseExcel(MultipartFile file) throws Exception {
        StringBuilder sb = new StringBuilder();
        List<Map<Integer, String>> rows = EasyExcel.read(file.getInputStream()).sheet().doReadSync();
        for (Map<Integer, String> row : rows) {
            List<String> cells = new ArrayList<>();
            int maxCol = row.keySet().stream().mapToInt(Integer::intValue).max().orElse(-1);
            for (int i = 0; i <= maxCol; i++) {
                cells.add(row.getOrDefault(i, ""));
            }
            sb.append(String.join("\t", cells)).append("\n");
        }
        return sb.toString();
    }

    private String parseCsv(MultipartFile file) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * RAG：将知识库检索到的内容作为系统上下文注入，让模型基于系统知识回答
     */
    private void injectKnowledgeContext(ChatCompletionRequest.Builder builder, ChatRequestCommand command) {
        String query = null;
        if (command.getMessages() != null) {
            for (ChatRequestCommand.Message msg : command.getMessages()) {
                if (msg.getRole() != null && msg.getContent() != null
                        && Constants.Role.USER == Constants.Role.getInstanceByCode(msg.getRole())) {
                    query = msg.getContent();
                }
            }
        }
        if (UEmpty.isEmpty(query)) {
            return;
        }
        List<Document> hits;
        try {
            hits = ragService.searchSimilar(query, 5, ragSimilarityThreshold);
        } catch (Exception e) {
            log.warn("知识库检索失败，本次对话跳过 RAG 上下文: {}", e.getMessage());
            return;
        }
        if (UEmpty.isEmpty(hits)) {
            return;
        }
        StringBuilder context = new StringBuilder();
        context.append("以下是关于本项目(Freesia后台管理系统)知识库中检索到的相关资料，仅在回答\"系统相关问题\"时作为参考：\n\n");
        for (int i = 0; i < hits.size(); i++) {
            Document doc = hits.get(i);
            Object source = doc.getMetadata().get("source");
            Object type = doc.getMetadata().get("type");
            context.append("【片段").append(i + 1).append("】")
                    .append(source != null ? "（来源：" + source + "）" : "")
                    .append(type != null ? "【" + type + "】" : "")
                    .append("\n")
                    .append(doc.getText())
                    .append("\n\n");
        }
        context.append("\n请遵循以下规则：\n"
                + "1. 如果用户问题与本系统（功能、接口、架构、使用方法等）相关，请优先依据上述知识库内容回答，并注明信息来源。\n"
                + "2. 如果用户问题是通用知识或与知识库无关（如常识、通用技术问题等），请直接使用你自己的知识正常回答，不受上述检索内容限制。\n"
                + "3. 如果上述检索内容与用户问题明显无关，请忽略它们，不要编造或生搬硬套。\n");
        builder.addSystemMessage(context.toString());
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
