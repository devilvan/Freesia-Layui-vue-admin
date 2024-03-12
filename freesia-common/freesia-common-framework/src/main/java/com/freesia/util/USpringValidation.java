package com.freesia.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
