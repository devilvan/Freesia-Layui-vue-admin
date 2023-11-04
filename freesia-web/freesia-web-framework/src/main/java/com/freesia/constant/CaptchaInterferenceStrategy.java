package com.freesia.constant;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 验证码干扰策略 枚举类
 * @date 2023-11-04
 */
@Getter
@AllArgsConstructor
public enum CaptchaInterferenceStrategy {
    /**
     * 线段干扰
     */
    LINE(LineCaptcha.class),
    /**
     * 线圈干扰
     */
    CIRCLE(CircleCaptcha.class),
    /**
     * 扭曲干扰
     */
    SHEAR(ShearCaptcha.class);

    private final Class<? extends AbstractCaptcha> strategy;
}
