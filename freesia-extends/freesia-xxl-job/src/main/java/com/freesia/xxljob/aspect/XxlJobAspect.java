package com.freesia.xxljob.aspect;

import com.freesia.constant.Constants;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Bliss.Wu
 * @Description XXL-JOB调度器 切面
 * @date 2026-02-27
 */
@Slf4j
@Aspect
@Component
public class XxlJobAspect {
    @Around(value = "@annotation(xxlJob)")
    protected Object around(ProceedingJoinPoint proceedingJoinPoint, XxlJob xxlJob) throws Throwable {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YMD_HMS_SSS);
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object proceed;
        long start = System.currentTimeMillis();
        log.info("********方法：{}，开始时间：{}********", method.getName(), simpleDateFormat.format(new Date(start)));
        try {
            proceed = proceedingJoinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.info("********方法：{}，结束时间：{}，消耗时间：{}********",
                    method.getName(), simpleDateFormat.format(new Date(end)), (end - start) + "ms");
        }
        return proceed;
    }
}
