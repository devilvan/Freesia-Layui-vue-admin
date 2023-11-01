package com.freesia.annotation;

import java.lang.annotation.*;

/**
 * @author Evad.Wu
 * @Description 数据权限列，一个注解只能对应一个模板
 * {@link com.freesia.annotation.DataPermission}
 * {@link com.freesia.handler.PlusDataPermissionHandler}
 * @date 2023-09-04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataColumn {

    /**
     * 占位符关键字
     */
    String[] key();

    /**
     * 占位符替换值
     */
    String[] value();

}
