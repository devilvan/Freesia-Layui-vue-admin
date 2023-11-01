package com.freesia.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author Evad.Wu
 * @Description 下载资源 通知类
 * @date 2022-09-12
 */
@Slf4j
@Aspect
@Component
public class DownloadAspect {
    @Around(value = "execution(public void com.freesia.component.DownloadComponent.download(..))")
    protected Object around(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args = proceedingJoinPoint.getArgs();
        String filePath = String.valueOf(args[1]);
        log.info("导出路径为：" + filePath);
        Object proceed = null;
        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return proceed;
    }
}
