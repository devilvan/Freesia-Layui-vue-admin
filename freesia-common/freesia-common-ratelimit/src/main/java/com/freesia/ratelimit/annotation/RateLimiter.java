package com.freesia.ratelimit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author Evad.Wu
 * @Description 请求限流-注解（基于Redis固定窗口计数，通过AOP切面生效）
 * @date 2026-08-13
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 时间窗口内允许的最大请求数
     */
    int count() default 10;

    /**
     * 时间窗口时长（配合 {@link #timeUnit()} 使用）
     */
    long time() default 60;

    /**
     * 时间窗口时长单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 触发限流时的提示消息（i18n编码）
     */
    String message() default "rate.limit.exceed";
}
