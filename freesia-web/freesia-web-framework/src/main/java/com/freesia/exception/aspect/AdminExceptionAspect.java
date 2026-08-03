package com.freesia.exception.aspect;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.http.HttpStatus;
import com.freesia.constant.UserModule;
import com.freesia.vo.R;
import lombok.extern.slf4j.Slf4j;
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
        String message = e.getMessage();
        log.error("所属模块：【{}】，请求地址：【{}】，错误信息：{}", UserModule.SubModule.LOGIN, request.getRequestURL(), message);
        return R.failed(HttpStatus.HTTP_UNAUTHORIZED, message);
    }
}
