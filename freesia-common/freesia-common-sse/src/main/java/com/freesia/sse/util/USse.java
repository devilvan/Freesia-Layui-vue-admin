package com.freesia.sse.util;

import com.freesia.dto.BaseSseMessageDto;
import com.freesia.sse.component.SseEmitterManager;
import com.freesia.util.USpring;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description SSE (Server-Sent Events)  工具类
 * @date 2024-10-22
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class USse {
    private static final SseEmitterManager SSE_MANAGER = USpring.getBean(SseEmitterManager.class);

    /**
     * 发布消息
     *
     * @param sseMessageDto 待发布的SSE消息对象
     * @param <T>           SSE消息实体类型
     */
    public static <T extends BaseSseMessageDto> void publish(T sseMessageDto) {
        SSE_MANAGER.publish(sseMessageDto);
    }

    /**
     * 发布消息（群发）
     *
     * @param sseMessageDto 待发布的SSE消息对象
     * @param <T>           SSE消息实体类型
     */
    public static <T extends BaseSseMessageDto> void publishAll(T sseMessageDto) {
        SSE_MANAGER.publishAll(sseMessageDto);
    }
}
