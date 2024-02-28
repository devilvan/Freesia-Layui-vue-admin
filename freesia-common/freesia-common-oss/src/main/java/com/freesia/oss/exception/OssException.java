package com.freesia.oss.exception;

import com.freesia.constant.DashboardModule;
import com.freesia.exception.ServiceException;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description OSS对象存储 异常类
 * @date 2024-02-27
 */
public class OssException extends ServiceException {
    @Serial
    private static final long serialVersionUID = 8261360291365883992L;

    public OssException(String code, Object... args) {
        super(DashboardModule.DASHBOARD_MANAGEMENT, code, args, null);
    }
}
