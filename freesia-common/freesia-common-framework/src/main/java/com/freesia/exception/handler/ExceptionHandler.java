package com.freesia.exception.handler;

import com.freesia.vo.R;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Evad.Wu
 * @Description 异常处理器接口（责任链模式）
 * @date 2026-08-11
 */
public interface ExceptionHandler {
    /**
     * 判断当前处理器是否支持该异常类型
     *
     * @param exception 异常对象
     * @return 是否支持
     */
    boolean supports(Exception exception);

    /**
     * 处理异常
     *
     * @param exception 异常对象
     * @param request   请求对象
     * @return 统一响应结果
     */
    R<?> handle(Exception exception, HttpServletRequest request);

    /**
     * 获取处理器优先级，数值越小优先级越高
     *
     * @return 优先级
     */
    int getOrder();
}
