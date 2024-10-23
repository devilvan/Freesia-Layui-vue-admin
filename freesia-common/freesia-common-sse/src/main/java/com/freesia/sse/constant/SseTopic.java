package com.freesia.sse.constant;

import cn.hutool.core.lang.copier.Copier;
import com.freesia.sse.handler.GlobalSseReceiveHandler;
import com.freesia.sse.handler.MessageReceiveHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description SSE (Server-Sent Events) 静态类
 * @date 2024-10-21
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public enum SseTopic {
    /**
     * 全局消息topic
     */
    GLOBAL_SSE("global:sse", GlobalSseReceiveHandler::new);

    /**
     * 消息键
     */
    private String key;
    /**
     * 消息接收处理器
     */
    private Copier<MessageReceiveHandler> listener;
}
