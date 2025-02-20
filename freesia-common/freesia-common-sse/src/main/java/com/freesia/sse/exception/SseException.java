package com.freesia.sse.exception;

import com.freesia.exception.ServiceException;
import com.freesia.sse.constant.SseModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 用户模块 异常类
 * @date 2023-08-13
 */
public class SseException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 8756197040766756051L;

    public SseException(String code, Object[] args) {
        super(SseModule.SSE_MANAGEMENT, code, args);
    }
}
