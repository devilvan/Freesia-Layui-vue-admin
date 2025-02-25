package com.freesia.redis.config;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.freesia.constant.Constants;
import com.freesia.desensization.handler.DesensitizeValueFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Evad.Wu
 * @Description redis 配置类
 * @date 2024-10-11
 */
@Configuration
public class RedisConfig implements WebMvcConfigurer {
    /**
     * 自定义redisTemplate
     * 改造为由fastjson管理，参考网址：https://blog.csdn.net/qq_33396608/article/details/108277220
     */
    @Bean(name = "freesiaRedisTemplate")
    public RedisTemplate<String, Object> freesiaRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // key serde/deserde都是String
        GenericToStringSerializer<String> stringRedisSerializer = new GenericToStringSerializer<>(String.class);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        // FastJsonRedisSerializer
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        FastJsonConfig fastJsonConfig = fastJsonRedisSerializer.getFastJsonConfig();
        SerializeConfig serializeConfig = fastJsonConfig.getSerializeConfig();
        //加入的localDatetime序列化，也可以不加（但是要用@JSONField(format = "yyyy-MM-dd HH:mm:ss")）格式化
        serializeConfig.put(LocalDateTime.class, (serializer, object, fieldName, fieldType, features) -> {
            SerializeWriter out = serializer.out;
            if (object == null) {
                out.writeNull();
                return;
            }
            out.writeString(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format((LocalDateTime) object));
        });
        fastJsonConfig.setSerializeConfig(serializeConfig);
        fastJsonConfig.setFeatures(Feature.SupportAutoType);
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteClassName);
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 基于SpringBoot2 对 RedisCacheManager 的自定义配置
     */
    @Bean
    @DependsOn(value = "freesiaRedisTemplate")
    public RedisCacheManager freesiaRedisCacheManager(@Autowired RedisTemplate<String, Object> freesiaRedisTemplate) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                // 设置key为String
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(freesiaRedisTemplate.getStringSerializer()))
                // 设置value 为自动转Json的Object
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(freesiaRedisTemplate.getValueSerializer()))
                // 不缓存null
                .disableCachingNullValues();
        // 构造一个redis缓存管理器
        return RedisCacheManager.RedisCacheManagerBuilder
                // Redis 连接工厂
                .fromConnectionFactory(Objects.requireNonNull(freesiaRedisTemplate.getConnectionFactory()))
                // 设置默认缓存配置
                .cacheDefaults(redisCacheConfiguration)
                // 配置同步修改或删除 put/evict
                .transactionAware()
                .build();
    }


    @Bean(value = "fastJsonHttpMessageConverter")
    public FastJsonHttpMessageConverter buildFastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat(Constants.YMD_HMS);
        // 解决循环引用导致树结构的children字段为空时依旧返回[]的问题
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        //解决Long转json精度丢失的问题
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        // 添加脱敏拦截器
        fastJsonConfig.setSerializeFilters(new DesensitizeValueFilter());
        fastJsonConfig.setSerializeConfig(serializeConfig);
        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return fastConverter;
    }
}
