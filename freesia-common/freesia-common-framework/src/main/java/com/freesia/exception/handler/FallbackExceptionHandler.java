package com.freesia.exception.handler;

import cn.hutool.http.HttpStatus;
import com.freesia.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Evad.Wu
 * @Description 兜底异常处理器（处理所有未被前面处理器匹配的异常）
 * @date 2026-08-11
 */
@Slf4j
@Component
@Order(Integer.MAX_VALUE)
public class FallbackExceptionHandler implements ExceptionHandler {

    @Override
    public boolean supports(Exception exception) {
        return true;
    }

    @Override
    public R<?> handle(Exception exception, HttpServletRequest request) {
        String message = exception.getMessage();
        log.error("请求地址：【{}】，系统异常：{}", request.getRequestURL(), message, exception);
        return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, "系统异常，请联系管理员");
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
