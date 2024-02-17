package com.freesia.annotation;

import java.lang.annotation.*;

/**
 * @Description 计算运行时间 注解
 * @author Evad.Wu
 * @date 2022-08-11
 */
@Documented
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RunTime {
    /**
     * 是否开启
     */
    boolean open() default true;
}
