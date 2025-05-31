package com.freesia.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.freesia.constant.Constants;
import com.freesia.desensization.annotation.Desensitize;
import com.freesia.desensization.handler.DesensitizeSerializer;
import com.freesia.oss.annotation.Domain;
import com.freesia.oss.seder.DomainSerializer;
import de.codecentric.boot.admin.server.utils.jackson.AdminServerModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.math.BigInteger;
import java.text.SimpleDateFormat;

/**
 * @author Evad.Wu
 * @Description Jackson 配置类
 * @date 2025-05-31
 */
@Slf4j
@Configuration
public class JacksonConfig {

//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
//        return builder -> {
//            // 全局配置序列化返回 JSON 处理
//            JavaTimeModule javaTimeModule = new JavaTimeModule();
//            javaTimeModule.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
//            javaTimeModule.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
//            javaTimeModule.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
//            javaTimeModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
//            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
//            javaTimeModule.addDeserializer(Date.class, new CustomDateDeserializer());
//            builder.modules(javaTimeModule);
//            builder.timeZone(TimeZone.getDefault());
//            log.info("初始化 jackson 配置");
//        };
//    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 设置日期格式
        objectMapper.setDateFormat(new SimpleDateFormat(Constants.YMD_HMS));
        // 添加Java8时间支持
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 禁用循环引用检测（相当于FastJSON的DisableCircularReferenceDetect）
        objectMapper.configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true);
        // 解决Long精度丢失问题
        String[] arr = new String[]{".*password$", ".*secret$", ".*key$", ".*token$", ".*credentials.*,", ".*vcap_services$"};
        SimpleModule module = new AdminServerModule(arr);
        module.addSerializer(BigInteger.class, ToStringSerializer.instance);
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModules(module, new JavaTimeModule());
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
