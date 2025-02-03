package com.freesia.exception;

import com.freesia.constant.UserModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 用户模块 异常类
 * @date 2023-08-13
 */
public class UserException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 8756197040766756051L;

    public UserException(String code, Object[] args) {
        super(UserModule.USER_MANAGEMENT, code, args);
    }
}
