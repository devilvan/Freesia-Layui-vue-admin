package com.freesia.log.aspect;

import com.alibaba.fastjson.JSONObject;
import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.constant.FlagConstant;
import com.freesia.log.annotation.LogRecord;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.UEmpty;
import com.freesia.util.UMessage;
import com.freesia.util.USpring;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.servlet.http.HttpServletResponseWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Evad.Wu
 * @Description 操作日志记录 切面类
 * @date 2024-08-02
 */
@Slf4j
@Aspect
@Component
public class LogRecordAspect {
    private final String clzName = LogRecordAspect.class.getName();

    @Around(value = "@annotation(logRecord))")
    protected Object around(ProceedingJoinPoint proceedingJoinPoint, LogRecord logRecord) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        Object proceed = null;
        String request = logRecordBefore(proceedingJoinPoint, uuid);
        try {
            proceed = proceedingJoinPoint.proceed();
            String response = logRecordAfter(proceedingJoinPoint, proceed, uuid);
            SysSensitiveLogBean logBean = USecurity.recordSensitiveLog(() -> {
                SysSensitiveLogBean sysSensitiveLogBean = new SysSensitiveLogBean();
                sysSensitiveLogBean.setModule(logRecord.module());
                sysSensitiveLogBean.setSubModule(logRecord.subModule());
                sysSensitiveLogBean.setType(logRecord.type());
                sysSensitiveLogBean.setResult(FlagConstant.SUCCESS);
                sysSensitiveLogBean.setContextOld(request);
                sysSensitiveLogBean.setContext(response);
                String message = logRecord.message();
                if (UEmpty.isNotEmpty(message)) {
                    sysSensitiveLogBean.setRemark(UMessage.message(message));
                }
                return sysSensitiveLogBean;
            });
            USpring.context().publishEvent(logBean);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return proceed;
    }

    /**
     * 前置执行方法
     *
     * @param proceedingJoinPoint 切点
     * @param uuid                方法ID
     */
    private String logRecordBefore(ProceedingJoinPoint proceedingJoinPoint, String uuid) {
        String result = null;
        try {
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            Object[] args = proceedingJoinPoint.getArgs();
            String[] parameterNames = methodSignature.getParameterNames();
            Map<String, Object> paramMap = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                String parameterName = parameterNames[i];
                if (!(arg instanceof ExtendedServletRequestDataBinder || arg instanceof HttpServletResponseWrapper)) {
                    paramMap.put(parameterName, arg);
                }
                if (!paramMap.isEmpty()) {
                    result = JSONObject.toJSONString(paramMap);
                    log.info("---{}---\n[{}]方法：{}\n入参：{}", clzName, uuid, proceedingJoinPoint.getSignature(), result);
                }
            }
        } catch (Exception e) {
            log.error("\n---AOP Error---\n[{}] logRecordBefore错误报文: {}", uuid, e.toString());
        }
        return result;
    }

    /**
     * 后置执行方法
     *
     * @param proceedingJoinPoint 切点
     * @param proceed             方法执行结果
     * @param uuid                方法ID
     */
    private String logRecordAfter(ProceedingJoinPoint proceedingJoinPoint, Object proceed, String uuid) {
        String result = null;
        try {
            if (UEmpty.isNotNull(proceed)) {
                result = JSONObject.toJSONString(proceed);
                log.info("---{}---\n[{}]方法：{}\n返回值：{}", clzName, uuid, proceedingJoinPoint.getSignature(), result);
            }
        } catch (Exception e) {
            log.error("\n---{} Error---\n[{}] logRecordAfter错误报文: {}", clzName, uuid, e.toString());
        }
        return result;
    }

}
