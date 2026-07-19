package com.freesia.deepseek.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.freesia.deepseek.dto.ChatRequestCommand;
import io.github.pigmesh.ai.deepseek.core.DeepSeekClient;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionRequest;
import io.github.pigmesh.ai.deepseek.core.chat.ChatCompletionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author Evad.Wu
 * @Description Deepseek交互式对话 控制类
 * @date 2026-07-19
 */
@CrossOrigin
@RestController
@RequestMapping("/api/deepseek/chat")
@RequiredArgsConstructor
@Tag(name = "DeepseekChatController", description = "Deepseek交互式对话 控制器")
public class DeepseekChatController {
    private final DeepSeekClient deepSeekClient;

    @SaIgnore
    @Operation(summary = "流式对话", description = "支持多轮对话的流式SSE接口")
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
                switch (msg.getRole()) {
                    case "system":
                        builder.addSystemMessage(msg.getContent());
                        break;
                    case "assistant":
                        builder.addAssistantMessage(msg.getContent());
                        break;
                    case "user":
                    default:
                        builder.addUserMessage(msg.getContent());
                        break;
                }
            }
        }

        return deepSeekClient.chatFluxCompletion(builder.build());
    }
}
