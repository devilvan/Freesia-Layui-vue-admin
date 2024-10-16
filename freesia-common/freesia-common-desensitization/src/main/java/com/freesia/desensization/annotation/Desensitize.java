package com.freesia.desensization.annotation;


import com.freesia.constant.DesensitizedType;

import java.lang.annotation.*;

/**
 * @author Evad.Wu
 * @Description 自定义数据脱敏 注解
 * @date 2023-03-12
 */
@Documented
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Desensitize {
    /**
     * 数据脱敏策略（类型），默认无
     */
    DesensitizedType[] strategy() default DesensitizedType.NONE;
}
