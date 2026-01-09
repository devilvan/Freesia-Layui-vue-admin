package com.freesia.convert;

import com.freesia.convert.impl.ReflectionConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Evad.Wu
 * @Description （转换器模式）泛型转换器 工厂
 * @date 2026-01-04
 */
@Component
@SuppressWarnings(value = "all")
public class ConverterFactory {
    private final Map<ClassPair, Converter> converters = new ConcurrentHashMap<>();

    public <SOURCE, TARGET> Converter<SOURCE, TARGET> getConverter(Class<SOURCE> sourceClass, Class<TARGET> targetClass) {
        return converters.computeIfAbsent(new ClassPair(sourceClass, targetClass),
                k -> new ReflectionConverter<>(sourceClass, targetClass));
    }

    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassPair {
        /**
         * 源对象类
         */
        private Class<?> source;
        /**
         * 目标对象类
         */
        private Class<?> target;
    }
}
