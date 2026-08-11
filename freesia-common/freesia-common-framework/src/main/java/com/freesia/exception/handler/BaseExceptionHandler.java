package com.freesia.exception.handler;

import cn.hutool.http.HttpStatus;
import com.freesia.exception.BaseException;
import com.freesia.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Evad.Wu
 * @Description BaseException 异常处理器
 * @date 2026-08-11
 */
@Slf4j
@Component
@Order(1)
public class BaseExceptionHandler implements ExceptionHandler {

    @Override
    public boolean supports(Exception exception) {
        return exception instanceof BaseException;
    }

    @Override
    public R<?> handle(Exception exception, HttpServletRequest request) {
        BaseException e = (BaseException) exception;
        String message = e.getMessage();
        log.error("所属模块：【{}】请求地址：【{}】，错误信息：【{}】",
                e.getModule(), request.getRequestURL(), message);
        return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
