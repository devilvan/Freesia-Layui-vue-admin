package com.freesia.util;

import cn.hutool.extra.spring.SpringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Evad.Wu
 * @Description Spring操作 工具类
 * @date 2023-08-12
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class USpring extends SpringUtil {
    /**
     * 获取aop代理对象
     *
     * @param invoker 获取的代理对象的类型
     * @return aop代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    /**
     * 获取spring上下文
     *
     * @return app上下文对象
     */
    public static ApplicationContext context() {
        return getApplicationContext();
    }
}
