package com.freesia.redis.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

/**
 * @author Evad.Wu
 * @Description redis 配置类
 * @date 2024-10-11
 */
@Configuration
public class RedisConfig implements WebMvcConfigurer {

    /**
     * 自定义redisTemplate，使用Jackson代替FastJSON
     */
    @Bean(name = "freesiaRedisTemplate")
    public RedisTemplate<String, Object> freesiaRedisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            ObjectMapper objectMapper) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 配置key的序列化方式为String
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 创建Jackson序列化器
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        // 配置ObjectMapper
        ObjectMapper redisObjectMapper = objectMapper.copy();
        // 启用类型信息以便反序列化
        redisObjectMapper.activateDefaultTyping(
                redisObjectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        jacksonSerializer.setObjectMapper(redisObjectMapper);

        // 设置value的序列化方式
        redisTemplate.setValueSerializer(jacksonSerializer);
        redisTemplate.setHashValueSerializer(jacksonSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 自定义RedisCacheManager
     */
    @Bean
    @DependsOn(value = "freesiaRedisTemplate")
    public RedisCacheManager freesiaRedisCacheManager(
            @Autowired RedisTemplate<String, Object> freesiaRedisTemplate) {

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                // 设置key为String
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        freesiaRedisTemplate.getStringSerializer()))
                // 设置value为Jackson序列化的Object
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        freesiaRedisTemplate.getValueSerializer()))
                // 不缓存null
                .disableCachingNullValues();

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(Objects.requireNonNull(freesiaRedisTemplate.getConnectionFactory()))
                .cacheDefaults(redisCacheConfiguration)
                .transactionAware()
                .build();
    }
}