package com.freesia.util;

import cn.hutool.core.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Evad.Wu
 * @Description 反射 工具类
 * @date 2023-09-03
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UReflect extends ReflectUtil {

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String SEPARATOR = ".";

    private static final Map<Class<?>, Map<Class<? extends Annotation>, List<Field>>> CACHE = new ConcurrentHashMap<>();

    /**
     * 查找类及其父类中带有指定注解的属性（包括元注解）
     *
     * @param clazz          要检查的类
     * @param annotationType 要查找的注解类型
     * @return 包含所有匹配Field的列表
     */
    public static List<Field> findFieldsWithAnnotation(Class<?> clazz,
                                                       Class<? extends Annotation> annotationType) {
        return CACHE.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(annotationType, k -> findAllFieldsWithAnnotation(clazz, annotationType));
    }

    /**
     * 查找类及其父类中带有指定注解的属性（包括元注解）
     *
     * @param clazz          要检查的类
     * @param annotationType 要查找的注解类型
     * @return 包含所有匹配Field的列表
     */
    private static List<Field> findAllFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationType) {
        List<Field> result = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (AnnotatedElementUtils.hasAnnotation(field, annotationType)) {
                    result.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return result;
    }

    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StringUtils.split(propertyName, SEPARATOR)) {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
            object = invoke(object, getterMethodName);
        }
        return (E) object;
    }
}
