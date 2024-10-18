package com.freesia.idempotent.annotation;

import java.lang.annotation.*;

/**
 * @author Evad.Wu
 * @Description 幂等处理-防止重复提交 注解
 * @date 2024-09-25
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    /**
     * 间隔时间(Duration)，小于此时间视为重复提交
     */
    String interval() default "PT5S";

    /**
     * 重复提交时提示消息（i18n编码）
     */
    String message() default "idempotent.repeatSubmit";
}
