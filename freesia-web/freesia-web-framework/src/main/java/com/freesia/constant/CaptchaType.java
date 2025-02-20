package com.freesia.constant;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import com.freesia.handler.UnsignedCaptchaGenHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 验证码类型 枚举类
 * @date 2023-11-04
 */
@Getter
@AllArgsConstructor
public enum CaptchaType {
    /**
     * 符号计算验证码
     */
    CALCULATE(UnsignedCaptchaGenHandler.class),
    /**
     * 字符
     */
    CHAR(RandomGenerator.class);

    private final Class<? extends CodeGenerator> type;
}
