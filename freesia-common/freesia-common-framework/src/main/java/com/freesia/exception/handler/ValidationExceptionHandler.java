package com.freesia.exception.handler;

import cn.hutool.http.HttpStatus;
import com.freesia.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

/**
 * @author Evad.Wu
 * @Description 参数校验异常处理器
 * @date 2026-08-11
 */
@Slf4j
@Component
@Order(0)
public class ValidationExceptionHandler implements ExceptionHandler {

    @Override
    public boolean supports(Exception exception) {
        return exception instanceof MethodArgumentNotValidException
                || exception instanceof MethodArgumentTypeMismatchException
                || exception instanceof MissingServletRequestParameterException;
    }

    @Override
    public R<?> handle(Exception exception, HttpServletRequest request) {
        if (exception instanceof MethodArgumentNotValidException e) {
            String message = Objects.requireNonNull(
                    e.getBindingResult().getFieldError()).getDefaultMessage();
            log.error("参数校验失败，请求地址：【{}】，错误信息：【{}】", request.getRequestURL(), message);
            return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
        }
        if (exception instanceof MethodArgumentTypeMismatchException e) {
            String message = String.format("参数类型错误：'%s' 应为 '%s' 类型",
                    e.getName(), Objects.requireNonNull(e.getRequiredType()).getSimpleName());
            log.error("参数类型错误，请求地址：【{}】，错误信息：【{}】", request.getRequestURL(), message);
            return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
        }
        if (exception instanceof MissingServletRequestParameterException e) {
            String message = String.format("缺少必要参数：'%s'", e.getParameterName());
            log.error("缺少参数，请求地址：【{}】，错误信息：【{}】", request.getRequestURL(), message);
            return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
        }
        String message = exception.getMessage();
        log.error("参数校验异常，请求地址：【{}】，错误信息：【{}】", request.getRequestURL(), message);
        return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
