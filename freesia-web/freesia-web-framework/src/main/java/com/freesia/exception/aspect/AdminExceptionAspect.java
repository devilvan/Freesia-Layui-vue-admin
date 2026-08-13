package com.freesia.exception.aspect;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.http.HttpStatus;
import com.freesia.constant.UserModule;
import com.freesia.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Evad.Wu
 * @Description 管理模块-异常统一处理 切面类
 * @date 2023-08-25
 */
@Slf4j
@Component
@RestControllerAdvice
@Order(1)
public class AdminExceptionAspect {
    /**
     * 权限管理模块异常处理
     *
     * @param request 请求头
     * @param e       异常
     * @return 形式返回
     */
    @ExceptionHandler(NotLoginException.class)
    public R<Void> notLoginException(HttpServletRequest request, NotLoginException e) {
        // 根据未登录类型给出差异化提示，前端统一处理并引导重新登录
        String message = switch (e.getType()) {
            case NotLoginException.TOKEN_TIMEOUT -> "登录已过期，请重新登录";
            case NotLoginException.INVALID_TOKEN -> "登录状态无效，请重新登录";
            case NotLoginException.BE_REPLACED -> "账号已在别处登录，请重新登录";
            case NotLoginException.KICK_OUT -> "账号已被强制下线，请重新登录";
            case NotLoginException.TOKEN_FREEZE -> "账号已被冻结，请联系管理员";
            default -> "未登录，请先登录";
        };
        log.error("所属模块：【{}】，请求地址：【{}】，错误信息：{}", UserModule.SubModule.LOGIN, request.getRequestURL(), message);
        return R.failed(HttpStatus.HTTP_UNAUTHORIZED, message);
    }
}
