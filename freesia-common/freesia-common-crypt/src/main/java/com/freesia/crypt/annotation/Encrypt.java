package com.freesia.crypt.annotation;

import java.lang.annotation.*;

/**
 * @author Evad.Wu
 * @Description 响应报文加密 注解
 * @date 2024-04-07
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypt {
}
