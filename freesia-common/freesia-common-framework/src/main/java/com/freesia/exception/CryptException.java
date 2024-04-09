package com.freesia.exception;

import com.freesia.constant.CryptModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 加密/解密模块 异常类
 * @date 2024-03-23
 */
public class CryptException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 8756197040766756051L;

    public CryptException(String code, Object... args) {
        super(CryptModule.CRYPT_MANAGEMENT, code, args, null);
    }
}
