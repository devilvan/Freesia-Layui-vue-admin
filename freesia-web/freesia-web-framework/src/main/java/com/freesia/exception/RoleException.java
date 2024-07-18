package com.freesia.exception;

import com.freesia.constant.RoleModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 部门管理模块 异常类
 * @date 2024-07-09
 */
public class RoleException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 8756197040766756051L;

    public RoleException(String code, Object... args) {
        super(RoleModule.ROLE_MANAGEMENT, code, args, null);
    }
}
