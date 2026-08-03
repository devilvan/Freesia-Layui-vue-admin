package com.freesia.validation.annotation;

import com.freesia.validation.handler.PhoneCnConstraintValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * @author Evad.Wu
 * @Description 验证中国大陆手机号 注解
 * @date 2024-03-11
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PhoneCnConstraintValidator.class)
public @interface Phone_CN {
    /**
     * 是否必填
     *
     * @return 是否必填
     */
    boolean required() default true;

    /**
     * 验证失败显示信息
     *
     * @return 验证失败显示信息
     */
    String message() default "";

    /**
     * 校验组
     *
     * @return 校验组
     */
    Class<?>[] groups() default {};

    /**
     * 其他参数
     *
     * @return 其他参数
     */
    Class<? extends Payload>[] payload() default {};
}
