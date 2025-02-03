package com.freesia.exception;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 验证码错误 异常类
 * @date 2023-08-13
 */
public class CaptchaException extends UserException {
    @Serial
    private static final long serialVersionUID = -2626363894568717960L;

    public CaptchaException() {
        super("user.jcaptcha.error", null);
    }
}
