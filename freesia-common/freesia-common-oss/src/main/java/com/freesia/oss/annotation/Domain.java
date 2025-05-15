package com.freesia.oss.annotation;

import java.lang.annotation.*;

/**
 * @author Evad.Wu
 * @Description FastJSON序列化时将URL转化为域名模式 注解
 * @date 2025-05-14
 */
@Documented
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Domain {
    /**
     * @return 文件存储服务类型
     */
    String configKey() default "";
}
