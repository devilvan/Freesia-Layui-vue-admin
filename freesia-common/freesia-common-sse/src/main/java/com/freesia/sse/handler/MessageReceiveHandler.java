package com.freesia.sse.handler;

import org.springframework.data.redis.connection.MessageListener;

/**
 * @author Evad.Wu
 * @Description SSE (Server-Sent Events) 消息接收 处理类
 * @date 2024-10-22
 */
public interface MessageReceiveHandler extends MessageListener {
}
