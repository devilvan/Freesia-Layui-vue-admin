package com.freesia.exception.handler;

import com.freesia.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description 异常处理器链（责任链模式）
 * @date 2026-08-11
 */
@Slf4j
@Getter
@Component
public class ExceptionHandlerChain {
    private final List<ExceptionHandler> handlers;

    public ExceptionHandlerChain(List<ExceptionHandler> handlers) {
        this.handlers = handlers.stream()
                .filter(h -> !(h instanceof FallbackExceptionHandler))
                .sorted(Comparator.comparingInt(ExceptionHandler::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * 处理异常，若链中无处理器匹配则使用兜底处理器
     *
     * @param exception 异常对象
     * @param request   请求对象
     * @return 统一响应结果
     */
    public R<?> handle(Exception exception, HttpServletRequest request) {
        for (ExceptionHandler handler : handlers) {
            if (handler.supports(exception)) {
                return handler.handle(exception, request);
            }
        }
        log.error("未找到匹配的异常处理器，异常类型：{}", exception.getClass().getName());
        return R.failed("系统异常");
    }

    /**
     * 处理异常，若链中无处理器匹配则向上抛出，交由其他 @RestControllerAdvice 处理
     *
     * @param exception 异常对象
     * @param request   请求对象
     * @return 统一响应结果
     */
    public R<?> handleOrRethrow(Exception exception, HttpServletRequest request) {
        for (ExceptionHandler handler : handlers) {
            if (handler.supports(exception)) {
                return handler.handle(exception, request);
            }
        }
        throw new RuntimeException("未找到匹配的异常处理器，异常类型：" + exception.getClass().getName(), exception);
    }
}
