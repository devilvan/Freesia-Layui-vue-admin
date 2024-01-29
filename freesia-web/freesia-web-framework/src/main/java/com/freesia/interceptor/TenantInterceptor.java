package com.freesia.interceptor;

import org.apache.ibatis.plugin.Interceptor;

/**
 * @author Evad.Wu
 * @Description Mybatis-Plus 租户拦截器
 * @date 2024-01-29
 */
//@Intercepts({
//        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
//        @Signature(type = Page.class, method = "getSql", args = {MappedStatement.class, Object.class, boolean.class})
//})
public class TenantInterceptor {
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//        // 获取租户ID或其他相关信息
//        String tenantId = getTenantId(); // 这是一个假设的方法，你需要实现它来获取租户ID
//
//        // 你可以根据需要修改以下代码，例如，你可能想要基于租户ID来过滤某些操作或更改SQL。
//        if (/* some condition based on tenantId */) {
//            return invocation.proceed(); // 继续执行原始操作
//        } else {
//            // 修改SQL或进行其他操作
//            return /* some value */;
//        }
//    }
//
//    @Override
//    public Object plugin(Object target) {
//        return Plugin.wrap(target, this);
//    }
//
//    @Override
//    public void setProperties(Properties properties) {
//        Interceptor.super.setProperties(properties);
//    }
}
