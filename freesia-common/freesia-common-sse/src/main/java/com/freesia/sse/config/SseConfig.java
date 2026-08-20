package com.freesia.sse.config;

import com.freesia.sse.component.SseEmitterManager;
import com.freesia.sse.constant.SseTopic;
import com.freesia.sse.handler.MessageReceiveHandler;
import com.freesia.sse.properties.SseProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author Evad.Wu
 * @Description SSE (Server-Sent Events) 配置类
 * @date 2024-10-21
 */
@AutoConfiguration
@ConditionalOnProperty(value = "sse.enabled", havingValue = "true")
@EnableConfigurationProperties(SseProperties.class)
public class SseConfig {
    @Bean
    public SseEmitterManager sseEmitterManager() {
        return new SseEmitterManager();
    }

    /**
     * 整合Redis添加消息订阅/发布功能
     *
     * @param redisConnectionFactory Redis连接工厂
     * @return Redis消息监听存储器
     */
    @Bean(value = "redisMessageListenerContainer")
    public RedisMessageListenerContainer buildRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        SseTopic[] sseTopics = SseTopic.values();
        for (SseTopic sseTopic : sseTopics) {
            MessageReceiveHandler messageReceiveHandler = sseTopic.getListener().copy();
            MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(messageReceiveHandler, MessageListenerAdapter.ORIGINAL_DEFAULT_LISTENER_METHOD);
            redisMessageListenerContainer.addMessageListener(messageListenerAdapter, new ChannelTopic(sseTopic.getKey()));
        }
        return redisMessageListenerContainer;
    }
}
