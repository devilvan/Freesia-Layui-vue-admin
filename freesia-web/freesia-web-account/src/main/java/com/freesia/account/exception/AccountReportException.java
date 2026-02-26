package com.freesia.account.exception;

import com.freesia.account.constant.AccountModule;
import com.freesia.exception.ServiceException;

import java.io.Serial;

/**
 * @author Bliss.Wu
 * @Description 记账-报表 异常类
 * @date 2026-02-26
 */
public class AccountReportException extends ServiceException {
    @Serial
    private static final long serialVersionUID = -6844820033582191781L;

    public AccountReportException(String code) {
        super(AccountModule.ACCOUNT_REPORT_MANAGEMENT, code, null);
    }

    public AccountReportException(String code, Object[] args) {
        super(AccountModule.ACCOUNT_REPORT_MANAGEMENT, code, args);
    }
}
