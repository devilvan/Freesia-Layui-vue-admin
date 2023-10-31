package com.freesia.exception;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 验证码过期 异常类
 * @date 2023-08-13
 */
public class CaptchaExpireException extends UserException {
    @Serial
    private static final long serialVersionUID = -2626363894568717960L;

    public CaptchaExpireException() {
        super("user.jcaptcha.expire");
    }
}
