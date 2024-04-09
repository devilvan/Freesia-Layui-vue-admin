package com.freesia.util;

import cn.hutool.core.util.ReflectUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description Spring Validation 相关方法 工具类
 * @date 2024-03-11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class USpringValidation {
    public static final Pattern PHONE_CN_PATTERN = Pattern.compile("^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$");

    /**
     * 校验中国大陆手机号码
     *
     * @param value 手机号码
     * @return 校验结果
     */
    public static boolean phoneCn(String value) {
        if (UEmpty.isEmpty(value)) {
            return false;
        }
        Matcher matcher = PHONE_CN_PATTERN.matcher(value);
        return matcher.matches();
    }

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
            final String message = constraintViolation.getMessage();
            // 获取校验失败的字段
            String field = getField(data.getClass(), constraintViolation);
            // 获取校验失败的值
            final Object invalidValue = constraintViolation.getInvalidValue();
            return UMessage.message("validation.error.msg",
                    UMessage.message(message), field, invalidValue);
        }).collect(Collectors.toList());
    }

    /**
     * 根据校验失败的字段，获取@Schema注解中的描述
     *
     * @param <T>                 数据类型
     * @param dataType            数据的Class
     * @param constraintViolation 校验失败的结果
     * @return 描述
     */
    private static <T> String getField(Class<?> dataType, ConstraintViolation<T> constraintViolation) {
        final String property = constraintViolation.getPropertyPath().toString();
        String field = ReflectUtil.getField(dataType, property)
                .getAnnotation(Schema.class).description();
        if (UEmpty.isEmpty(field)) {
            field = property;
        }
        return field;
    }
}
