package com.freesia.annotation;

import java.lang.annotation.*;

/**
 * @author Evad.Wu
 * @Description 数据权限组
 * {@link com.freesia.handler.PlusDataPermissionHandler}
 * {@link DataColumn}
 * @date 2023-09-04
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    DataColumn[] value();

}
