package com.freesia.component;

import com.freesia.exception.handler.ExceptionHandlerChain;
import com.freesia.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Evad.Wu
 * @Description 全局异常统一处理切面类（责任链模式）
 * @date 2023-08-25
 */
@Slf4j
@Component
@RestControllerAdvice
@Order(100)
public class CommonExceptionAspect {
    private final ExceptionHandlerChain handlerChain;

    public CommonExceptionAspect(ExceptionHandlerChain handlerChain) {
        this.handlerChain = handlerChain;
    }

    /**
     * 全局异常处理入口，通过责任链分发到具体处理器。
     * 若链中无任何处理器支持该异常，则向上抛出，交由其他 @RestControllerAdvice 处理。
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e, HttpServletRequest request) {
        return handlerChain.handleOrRethrow(e, request);
    }
}
