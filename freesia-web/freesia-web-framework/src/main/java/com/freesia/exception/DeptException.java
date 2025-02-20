package com.freesia.exception;

import com.freesia.constant.DeptModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 部门管理模块 异常类
 * @date 2024-07-09
 */
public class DeptException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 8756197040766756051L;

    public DeptException(String code, Object[] args) {
        super(DeptModule.DEPT_MANAGEMENT, code, args);
    }
}
