package com.freesia.notice.exception;

import com.freesia.exception.ServiceException;
import com.freesia.notice.constant.NoticeModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 通知模块 异常类
 * @date 2025-09-23
 */
public class NoticeException extends ServiceException {
    @Serial
    private static final long serialVersionUID = 8756197040766756051L;

    public NoticeException(String code, Object[] args) {
        super(NoticeModule.NOTICE_MANAGEMENT, code, args);
    }
}
