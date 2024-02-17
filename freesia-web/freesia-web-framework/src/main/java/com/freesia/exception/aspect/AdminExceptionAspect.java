package com.freesia.exception.aspect;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.http.HttpStatus;
import com.freesia.constant.SysModule;
import com.freesia.constant.UserModule;
import com.freesia.exception.ServiceException;
import com.freesia.exception.UserException;
import com.freesia.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Evad.Wu
 * @Description 管库模块-异常统一处理 切面类
 * @date 2023-08-25
 */
@Slf4j
@Component
@RestControllerAdvice
public class AdminExceptionAspect {
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
        log.error("所属模块：【{}】请求地址：【{}】，错误信息：{}", e.getModule(), request.getRequestURL(), message);
        return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
    }

    /**
     * 用户模块异常
     *
     * @param request 异常的请求
     * @param e       捕获的异常
     * @return 形式返回
     */
    @ExceptionHandler(UserException.class)
    public R<Object> userException(HttpServletRequest request, UserException e) {
        String message = e.getMessage();
        log.error("所属模块：【{}】请求地址：【{}】，错误信息：{}", e.getModule(), request.getRequestURL(), message);
        return R.failed(HttpStatus.HTTP_INTERNAL_ERROR, message);
    }

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
