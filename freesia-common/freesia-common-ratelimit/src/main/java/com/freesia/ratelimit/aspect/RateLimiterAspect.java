package com.freesia.ratelimit.aspect;

import com.freesia.constant.CacheConstant;
import com.freesia.exception.ServiceException;
import com.freesia.ratelimit.annotation.RateLimiter;
import com.freesia.redis.util.URedis;
import com.freesia.util.UMessage;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 请求限流-切面类（基于Redis固定窗口计数，Lua脚本原子递增+过期）
 * @date 2026-08-13
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    /**
     * 固定窗口计数脚本：首次请求时递增为1并设置过期时间，窗口内计数递增
     */
    private static final String RATE_LIMIT_SCRIPT =
            "local current = redis.call('incr', KEYS[1]);"
                    + "if tonumber(current) == 1 then"
                    + "    redis.call('expire', KEYS[1], ARGV[1]);"
                    + "end;"
                    + "return current;";

    @Around(value = "@annotation(rateLimiter)")
    protected Object around(ProceedingJoinPoint proceedingJoinPoint, RateLimiter rateLimiter) throws Throwable {
        if (rateLimiter.count() <= 0) {
            throw new ServiceException("限流配置错误：count 必须大于 0");
        }
        long windowSeconds = rateLimiter.timeUnit().toSeconds(rateLimiter.time());
        if (windowSeconds < 1) {
            throw new ServiceException("限流配置错误：时间窗口不能小于 1 秒");
        }
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        // 构造key：rate_limit:全限定类名.方法名
        String key = CacheConstant.RATE_LIMIT_KEY + signature.getDeclaringTypeName() + "." + signature.getName();
        Long current = URedis.executeLua(RATE_LIMIT_SCRIPT, List.of(key), windowSeconds);
        // 超过限制则拒绝本次请求
        if (current != null && current > rateLimiter.count()) {
            String message = UMessage.message(rateLimiter.message());
            log.warn("\n---RateLimiter---\n[{}] 触发限流, 当前次数: {}, 限制: {}次/{}秒",
                    signature, current, rateLimiter.count(), windowSeconds);
            throw new ServiceException(message);
        }
        return proceedingJoinPoint.proceed();
    }
}
