package com.freesia.exception.handler;

import cn.hutool.http.HttpStatus;
import com.freesia.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Evad.Wu
 * @Description IOException 异常处理器
 * @date 2026-08-11
 */
@Slf4j
@Component
@Order(3)
public class IOExceptionHandler implements ExceptionHandler {

    @Override
    public boolean supports(Exception exception) {
        return exception instanceof IOException;
    }

    @Override
    public R<?> handle(Exception exception, HttpServletRequest request) {
        String message = exception.getMessage();
        log.error("请求地址：【{}】，IO错误信息：{}", request.getRequestURL(), message);
        return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
