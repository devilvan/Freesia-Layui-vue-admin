package com.freesia.util;

import cn.hutool.core.util.ReflectUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description Spring Validation 相关方法 工具类
 * @date 2024-03-11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class USpringValidation {
    /**
     * 手动调用Validate的校验方法
     *
     * @param data 待校验的数据
     * @param <T>  待校验的数据的类型
     * @return 校验失败的集合
     */
    public static <T> Set<ConstraintViolation<T>> validate(T data) {
        return Validation.buildDefaultValidatorFactory().getValidator().validate(data);
    }

    /**
     * 校验数据并返回校验结果
     *
     * @param data 待校验的数据
     * @param <T>  数据类型
     * @return 校验结果
     */
    public static <T> List<String> errorMsg(T data) {
        return validate(data).stream().map(constraintViolation -> {
            // 获取校验失败的信息
            final String messageCode = constraintViolation.getMessage();
            // 获取校验失败的字段
            Class<?> dataType = data.getClass();
            String property = constraintViolation.getPropertyPath().toString();
            String field = getFieldSchema(dataType, property);
            // 获取校验失败的值
            final Object invalidValue = constraintViolation.getInvalidValue();
            // 判断是否需要其他参数支持的注解
            ConstraintDescriptor<?> constraintDescriptor = constraintViolation.getConstraintDescriptor();
            Class<? extends Annotation> annotationClass = constraintDescriptor.getAnnotation().annotationType();
            if (annotationClass.isAssignableFrom(Length.class)) {
                return formatLength(messageCode, dataType, property, field, invalidValue);
            }
            return UMessage.message("validation.error.msg",
                    UMessage.message(messageCode), field, invalidValue);
        }).collect(Collectors.toList());
    }

    /**
     * 根据{@link Length}注解校验数据
     *
     * @param messageCode  消息键
     * @param dataType     待校验的数据Class
     * @param property     带校验的属性名
     * @param field        错误字段（中文/属性名）
     * @param invalidValue 错误值
     * @return 校验后的错误信息
     */
    private static String formatLength(String messageCode, Class<?> dataType, String property, String field, Object invalidValue) {
        Length fieldAnnotation = getFieldAnnotation(dataType, property, Length.class);
        int max = fieldAnnotation.max();
        int min = fieldAnnotation.min();
        return UMessage.message("validation.error.msg",
                UMessage.message(messageCode, min, max), field, invalidValue);
    }

    /**
     * 根据校验失败的字段，获取@Schema注解中的描述
     *
     * @param dataType 数据的Class
     * @param property 属性名称
     * @return 描述
     */
    private static String getFieldSchema(Class<?> dataType, String property) {
        String field = ReflectUtil.getField(dataType, property)
                .getAnnotation(Schema.class).description();
        if (UEmpty.isEmpty(field)) {
            field = property;
        }
        return field;
    }

    /**
     * 根据校验失败的字段，获取需要的校验注解
     *
     * @param <T>            数据类型
     * @param dataType       数据的Class
     * @param property       属性名称
     * @param annotationType 校验注解的类型
     * @return 描述
     */
    private static <T extends Annotation> T getFieldAnnotation(Class<?> dataType, String property, Class<T> annotationType) {
        return ReflectUtil.getField(dataType, property).getAnnotation(annotationType);
    }
}
