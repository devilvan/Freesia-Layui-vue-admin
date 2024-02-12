package com.freesia.exception;

import com.freesia.constant.TenantModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 租户模块 异常类
 * @date 2024-02-11
 */
public class TenantException extends BaseException {
    @Serial
    private static final long serialVersionUID = 9168523946223394482L;

    public TenantException(String code, Object... args) {
        super(TenantModule.TENANT_MANAGEMENT, code, args, null);
    }
}
