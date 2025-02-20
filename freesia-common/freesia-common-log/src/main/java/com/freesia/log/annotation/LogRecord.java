package com.freesia.log.annotation;

import java.lang.annotation.*;

/**
 * @author Evad.Wu
 * @Description 操作日志记录 注解
 * @date 2024-08-02
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogRecord {
    /**
     * @return 所属模块
     */
    String module();

    /**
     * @return 子模块
     */
    String subModule() default "";

    /**
     * @return 操作类型
     */
    String type() default "";

    /**
     * @return 消息
     */
    String message() default "";
}
