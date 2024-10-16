package com.freesia.net.exception;

import com.freesia.exception.ServiceException;
import com.freesia.net.constant.HttpClientModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description Http调用 异常类
 * @date 2024-01-18
 */
public class HttpClientException extends ServiceException {
    @Serial
    private static final long serialVersionUID = 711360139646536614L;

    public HttpClientException(String code, Object... args) {
        super(HttpClientModule.HTTP_CLIENT_MANAGEMENT, code, args, null);
    }
}
