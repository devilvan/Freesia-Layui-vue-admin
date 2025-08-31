package com.freesia.component;

import cn.hutool.http.HttpStatus;
import com.freesia.exception.ServiceException;
import com.freesia.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * @author Evad.Wu
 * @Description 管库模块-异常统一处理 切面类
 * @date 2023-08-25
 */
@Slf4j
@Component
@RestControllerAdvice
public class CommonExceptionAspect {
    /**
     * Validation 参数校验，验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
    }

    /**
     * 服务模块异常
     *
     * @param request 异常的请求
     * @param e       捕获的异常
     * @return 形式返回
     */
    @ExceptionHandler(ServiceException.class)
    public R<Object> serviceException(HttpServletRequest request, ServiceException e) {
        String message = e.getMessage();
        log.error("所属模块：【{}】请求地址：【{}】，错误信息：【{}】", e.getModule(), request.getRequestURL(), message);
        return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
    }

    /**
     * 不支持的编码异常
     *
     * @param request 异常的请求
     * @param e       捕获的异常
     * @return 形式返回
     */
    @ExceptionHandler(UnsupportedEncodingException.class)
    public R<Object> unsupportedEncodingException(HttpServletRequest request, UnsupportedEncodingException e) {
        String message = e.getMessage();
        log.error("请求地址：【{}】，错误信息：{}", request.getRequestURL(), message);
        return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
    }

    /**
     * 参数错误异常
     *
     * @param request 异常的请求
     * @param e       捕获的异常
     * @return 形式返回
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public R<Object> illegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
        String message = e.getMessage();
        log.error("请求地址：【{}】，错误信息：{}", request.getRequestURL(), message);
        return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
    }


}
