package com.freesia.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.freesia.constant.Constants;
import com.freesia.desensization.annotation.Desensitize;
import com.freesia.desensization.handler.DesensitizeSerializer;
import com.freesia.oss.annotation.Domain;
import com.freesia.oss.seder.DomainSerializer;
import com.freesia.serde.JacksonDateDeserializer;
import de.codecentric.boot.admin.server.utils.jackson.AdminServerModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Evad.Wu
 * @Description Jackson 配置类
 * @date 2025-05-31
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class JacksonConfig {
    private final AdminServerModule adminServerModule;

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 配置 ObjectMapper 忽略未知字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 设置日期格式
        objectMapper.setDateFormat(new SimpleDateFormat(Constants.YMD_HMS));
        // 设置时区（例如设置为系统默认时区）
        objectMapper.setTimeZone(TimeZone.getDefault());
        // 注册spring-admin服务端Module
        SimpleModule module = new SimpleModule();
        // 解决Long精度丢失问题
        module.addSerializer(BigInteger.class, ToStringSerializer.instance);
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(Constants.YMD_HMS);
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dtf));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dtf));
        module.addDeserializer(Date.class, new JacksonDateDeserializer());
        objectMapper.registerModules(module, new JavaTimeModule());
        if (adminServerModule != null) {
            objectMapper.registerModules(adminServerModule);
        }
        // 添加自定义序列化过滤器
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public Object findSerializer(Annotated am) {
                // 处理脱敏注解
                if (am.hasAnnotation(Desensitize.class)) {
                    return new DesensitizeSerializer();
                }
                // 处理Domain注解
                if (am.hasAnnotation(Domain.class)) {
                    return new DomainSerializer();
                }
                return super.findSerializer(am);
            }
        });
        // 处理中文乱码问题已在Spring Boot默认配置中解决
        return objectMapper;
    }
}
