package com.freesia.idempotent.aspect;

import cn.dev33.satoken.SaManager;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.Constants;
import com.freesia.crypt.util.UCrypt;
import com.freesia.exception.ServiceException;
import com.freesia.idempotent.annotation.Idempotent;
import com.freesia.net.util.UServlet;
import com.freesia.pojo.TableResult;
import com.freesia.redis.util.URedis;
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import com.freesia.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.*;

/**
 * @author Evad.Wu
 * @Description 幂等处理-防止重复提交 切面类
 * @date 2024-09-25
 */
@Slf4j
@Aspect
@Component
public class IdempotentAspect {
    public static final ThreadLocal<String> submitKeyThreadLocal = new ThreadLocal<>();

    @Around(value = "@annotation(idempotent))")
    protected Object around(ProceedingJoinPoint proceedingJoinPoint, Idempotent idempotent) {
        Object proceed = null;
        this.idempotentBeforeHandler(proceedingJoinPoint, idempotent);
        try {
            proceed = proceedingJoinPoint.proceed();
            this.idempotentAfterHandler(proceedingJoinPoint, proceed, idempotent);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error("\n---AOP Error---\n[{}]\nidempotentAspect错误报文: {}", proceedingJoinPoint.getSignature(), throwable.toString());
        }
        return proceed;
    }

    /**
     * 参数拼装
     *
     * @param paramsArray 方法入参
     * @return 处理、拼接后的字符串
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringJoiner params = new StringJoiner(" ");
        if (ArrayUtil.isEmpty(paramsArray)) {
            return params.toString();
        }
        for (Object o : paramsArray) {
            if (ObjectUtil.isNotNull(o) && !isFilterObject(o)) {
                params.add(JSONObject.toJSONString(o));
            }
        }
        return params.toString();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return MultipartFile.class.isAssignableFrom(clazz.getComponentType());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.values()) {
                return value instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }

    private void idempotentAfterHandler(ProceedingJoinPoint proceedingJoinPoint, Object proceed, Idempotent idempotent) {
        String repeatSubmitKey = submitKeyThreadLocal.get();
        if (UEmpty.isNotNull(proceed)) {
            try {
                if (proceed instanceof R<?> r) {
                    // 返回成功则保留
                    if (!r.isSuccess()) {
                        // 失败则释放锁，允许继续操作
                        URedis.delete(repeatSubmitKey);
                    }
                } else if (proceed instanceof TableResult<?> tableResult) {
                    if (!tableResult.isSuccess()) {
                        URedis.delete(repeatSubmitKey);
                    }
                }
            } finally {
                submitKeyThreadLocal.remove();
            }
        }
    }

    private void idempotentBeforeHandler(ProceedingJoinPoint proceedingJoinPoint, Idempotent idempotent) {
        Duration interval = Duration.parse(idempotent.interval());
        long intervalMillis = interval.toMillis();
        if (intervalMillis < 1000) {
            throw new ServiceException(UMessage.message("idempotent.min.intervalTime"));
        }
        // 获取方法入参
        String args = argsArrayToString(proceedingJoinPoint.getArgs());
        // 设置UUID
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        // 获取URL
        HttpServletRequest request = UServlet.getRequest();
        String uri = Optional.ofNullable(request).map(HttpServletRequest::getRequestURI).orElse("");
        String argsMd5Str = UCrypt.md5Encrypt(args);
        String repeatSubmitKey = CacheConstant.REPEAT_SUBMIT_KEY + uri + ":" + SaManager.getConfig().getTokenName() + ":" + argsMd5Str;
        submitKeyThreadLocal.set(repeatSubmitKey);
        String now = Constants.SDF_YMD.format(new Date());
        log.info("{} repeatSubmitKey：{}", now, repeatSubmitKey);
        boolean flag = URedis.setNx(repeatSubmitKey, uuid, interval);
        // 如果重复提交，提示失败
        if (!flag) {
            String message = UMessage.message(idempotent.message());
            log.error("\n---AOP Error---\n[{}]\nIdempotentBefore错误报文: {}", proceedingJoinPoint.getSignature(), message);
            throw new ServiceException(message);
        }
    }
}
