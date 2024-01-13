package com.freesia.exception;

import com.freesia.constant.DashboardModule;
import com.freesia.constant.SysModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 面板模块 异常类
 * @date 2023-12-26
 */
public class DashboardException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9168523946223394482L;

    public DashboardException(String code, Object... args) {
        super(DashboardModule.DASHBOARD_MANAGEMENT, code, args, null);
    }
}
