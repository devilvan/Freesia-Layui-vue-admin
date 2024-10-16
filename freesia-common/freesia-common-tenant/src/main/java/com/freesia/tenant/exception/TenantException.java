package com.freesia.tenant.exception;

import com.freesia.exception.ServiceException;
import com.freesia.tenant.constant.TenantModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 租户模块 异常类
 * @date 2024-02-11
 */
public class TenantException extends ServiceException {
    @Serial
    private static final long serialVersionUID = 9168523946223394482L;

    public TenantException(String code, Object... args) {
        super(TenantModule.TENANT_MANAGEMENT, code, args, null);
    }
}
