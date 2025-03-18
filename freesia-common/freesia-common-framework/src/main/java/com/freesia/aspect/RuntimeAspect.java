package com.freesia.aspect;

import com.freesia.annotation.RunTime;
import com.freesia.constant.Constants;
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
 * @author Evad.Wu
 * @Description 计算运行时间 通知组件
 * @date 2022-08-11
 */
@Slf4j
@Aspect
@Component
public class RuntimeAspect {
    @Around(value = "@annotation(runTime)")
    protected Object around(ProceedingJoinPoint proceedingJoinPoint, RunTime runTime) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object proceed = null;
        if (runTime.open()) {
            try {
                long start = System.currentTimeMillis();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YMD_HMS_SSS);
                log.info("********方法：{}，开始时间：{}********", method.getName(), simpleDateFormat.format(new Date(start)));
                proceed = proceedingJoinPoint.proceed();
                long end = System.currentTimeMillis();
                log.info("********方法：{}，结束时间：{}，消耗时间：{}********",
                        method.getName(), simpleDateFormat.format(new Date(end)), (end - start) + "ms");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return proceed;
    }
}
