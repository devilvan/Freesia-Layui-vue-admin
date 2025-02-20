package com.freesia.sse.handler;

import com.freesia.sse.constant.SseTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;

/**
 * @author Evad.Wu
 * @Description Topic：{@link SseTopic#GLOBAL_SSE} 消息接收 处理类
 * @date 2024-10-22
 */
@Slf4j
public class GlobalSseReceiveHandler implements MessageReceiveHandler {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 处理接收到的消息
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        log.info("SSE接收主题订阅消息topic：【{}】，message:【{}】", channel, body);
    }
}
