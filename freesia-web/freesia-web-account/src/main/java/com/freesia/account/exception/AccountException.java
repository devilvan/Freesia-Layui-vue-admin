package com.freesia.account.exception;

import com.freesia.account.constant.AccountModule;
import com.freesia.exception.ServiceException;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 记账模块 异常类
 * @date 2025-03-06
 */
public class AccountException extends ServiceException {
    @Serial
    private static final long serialVersionUID = -6844820033582191781L;

    public AccountException(String code, Object[] args) {
        super(AccountModule.ACCOUNT_MANAGEMENT, code, args);
    }
}
