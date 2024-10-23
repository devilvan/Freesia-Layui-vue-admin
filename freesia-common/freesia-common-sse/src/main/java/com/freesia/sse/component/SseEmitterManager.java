package com.freesia.sse.component;


import cn.hutool.core.collection.CollUtil;
import com.freesia.dto.BaseSseMessageDto;
import com.freesia.redis.util.URedis;
import com.freesia.util.UEmpty;
import com.freesia.util.UString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Evad.Wu
 * @Description SSE (Server-Sent Events) 连接管理 组件
 * 主题：{@link com.freesia.sse.constant.SseTopic}
 * @date 2024-10-21
 */
@Slf4j
public class SseEmitterManager {
    private static final Map<Long, Map<String, SseEmitter>> USER_TOKEN_EMITTER = new ConcurrentHashMap<>();

    private static SseEmitter.SseEventBuilder event() {
        return SseEmitter.event();
    }

    /**
     * 建立与指定用户的 SSE 连接
     *
     * @param userId 用户的唯一标识符，用于区分不同用户的连接
     * @param token  用户的唯一令牌，用于识别具体的连接
     * @return 返回一个 SseEmitter 实例，客户端可以通过该实例接收 SSE 事件
     */
    public SseEmitter connect(Long userId, String token) {
        // 通过userId获取SSE连接，每个用户可以拥有多个连接，通过token区分
        // 创建SSE实例，超时时间为0表示无限制
        Map<String, SseEmitter> tokenEmitter = USER_TOKEN_EMITTER.computeIfAbsent(userId, mapping -> new ConcurrentHashMap<>());
        SseEmitter sseEmitter = new SseEmitter(0L);
        tokenEmitter.put(token, sseEmitter);
        // 当 emitter 完成、超时或发生错误时，从映射表中移除对应的 token
        sseEmitter.onCompletion(() -> tokenEmitter.remove(token));
        sseEmitter.onTimeout(() -> tokenEmitter.remove(token));
        sseEmitter.onError(e -> tokenEmitter.remove(token));
        // 向客户端发送一条连接成功的事件
        try {
            sseEmitter.send(event().comment("connected"));
        } catch (IOException e) {
            e.printStackTrace();
            // 如果发送消息失败，则从映射表中移除 emitter
            tokenEmitter.remove(token);
        }
        return sseEmitter;
    }

    /**
     * 断开指定用户的 SSE 连接
     *
     * @param userId 用户的唯一标识符，用于区分不同用户的连接
     * @param token  用户的唯一令牌，用于识别具体的连接
     */
    public void disconnect(Long userId, String token) {
        Map<String, SseEmitter> tokenEmitter = USER_TOKEN_EMITTER.get(userId);
        if (UEmpty.isNotNull(tokenEmitter)) {
            try {
                SseEmitter sseEmitter = tokenEmitter.get(token);
                sseEmitter.send(event().comment("disconnected"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            tokenEmitter.remove(token);
        }
    }

    /**
     * 发布消息
     *
     * @param sseMessageDto 待发布的SSE消息对象
     * @param <T>           SSE消息实体类型
     */
    public <T extends BaseSseMessageDto> void publish(T sseMessageDto) {
        List<String> topicList = sseMessageDto.getTopicList();
        List<Long> userIdList = sseMessageDto.getUserIdList();
        String content = sseMessageDto.getContent();
        List<Long> unsentUserIds = new ArrayList<>();
        // 当前服务内用户,直接发送消息
        for (Long userId : userIdList) {
            if (USER_TOKEN_EMITTER.containsKey(userId)) {
                Map<String, SseEmitter> tokenEmitter = USER_TOKEN_EMITTER.get(userId);
                if (UEmpty.isNotEmpty(tokenEmitter)) {
                    Set<Map.Entry<String, SseEmitter>> entrySet = tokenEmitter.entrySet();
                    for (Map.Entry<String, SseEmitter> entry : entrySet) {
                        String token = entry.getKey();
                        SseEmitter sseEmitter = entry.getValue();
                        try {
                            sseEmitter.send(event().name("message").data(content));
                        } catch (Exception e) {
                            tokenEmitter.remove(token);
                        }
                    }
                }
                continue;
            }
            unsentUserIds.add(userId);
        }
        // 不在当前服务内用户,发布订阅消息
        if (UEmpty.isNotEmpty(unsentUserIds)) {
            URedis.send(topicList, content);
            log.info("SSE发送主题订阅消息topic：【{}】，Session keys:{}，message:【{}】", UString.join(topicList, ","), unsentUserIds, content);
        }
    }

    /**
     * 向所有的用户发布订阅的消息(群发)
     *
     * @param sseMessageDto 待发布的SSE消息对象
     */
    public <T extends BaseSseMessageDto> void publishAll(T sseMessageDto) {
        List<String> topicList = sseMessageDto.getTopicList();
        String content = sseMessageDto.getContent();
        URedis.send(topicList, content);
    }
}
