package com.freesia.convert.factory;

import com.freesia.convert.ReflectConverter;
import com.freesia.convert.impl.ReflectionReflectConverter;
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
public class ReflectConverterFactory {
    private final Map<ClassPair, ReflectConverter> converters = new ConcurrentHashMap<>();

    public <SOURCE, TARGET> ReflectConverter<SOURCE, TARGET> getConverter(Class<SOURCE> sourceClass, Class<TARGET> targetClass) {
        return converters.computeIfAbsent(new ClassPair(sourceClass, targetClass),
                k -> new ReflectionReflectConverter<>(sourceClass, targetClass));
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
