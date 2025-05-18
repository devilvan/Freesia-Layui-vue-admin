package com.freesia.oss.seder;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.freesia.oss.annotation.Domain;
import com.freesia.oss.pojo.OssFactory;
import com.freesia.oss.pojo.OssHandler;
import com.freesia.util.UEmpty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Bliss.Wu
 * @Description FastJSON序列化时将URL转化为IP模式 值过滤器
 * @date 2025-05-15
 */
public class DomainSeValueFilter implements ValueFilter {
    @Override
    public Object process(Object object, String name, Object value) {
        Class<?> clz = object.getClass();
        try {
            boolean flag = hasAnnotationInClassHierarchy(clz, Domain.class);
            Field field = clz.getDeclaredField(name);
            Domain domain = field.getAnnotation(Domain.class);
            if (domain == null) {
                return value;
            }
            String configKey = domain.configKey();
            OssHandler ossHandler;
            if (UEmpty.isNotEmpty(configKey)) {
                ossHandler = OssFactory.getInstance(configKey);
            } else {
                ossHandler = OssFactory.getInstance();
            }
            if (value instanceof String) {
                if (UEmpty.isEmpty(value)) {
                    return value;
                }
                return ossHandler.convertEndpoint2Domain(value.toString());
            }
        } catch (NoSuchFieldException e) {
            return value;
        }
        return value;
    }

    public static boolean hasAnnotationInClassHierarchy(Class<?> clazz, Class<? extends Annotation> annotationType) {
        // 检查当前类及其父类
        while (clazz != null && clazz != Object.class) {
            // 检查类上的注解
            if (AnnotationUtils.findAnnotation(clazz, annotationType) != null) {
                return clazz.getField();
            }

            // 检查所有字段上的注解
            ReflectionUtils.doWithFields(clazz, field -> {
                if (field.getAnnotation(annotationType) != null) {
                    throw new RuntimeException("Found annotation on field: " + field.getName());
                }
            });

            clazz = clazz.getSuperclass();
        }
        return false;
    }
}
