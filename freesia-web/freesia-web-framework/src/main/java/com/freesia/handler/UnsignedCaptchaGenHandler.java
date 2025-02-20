package com.freesia.handler;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.math.Calculator;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description 无符号验证码生成 处理类
 * @date 2023-11-04
 */
public class UnsignedCaptchaGenHandler implements CodeGenerator {
    @Serial
    private static final long serialVersionUID = -7909917989275750793L;
    /**
     * 运算符
     */
    private static final String SEPARATOR = "+-*";
    /**
     * 参与计算数字最大长度
     */
    private final int numberLength;

    public UnsignedCaptchaGenHandler() {
        this(2);
    }

    public UnsignedCaptchaGenHandler(int numberLength) {
        this.numberLength = numberLength;
    }

    @Override
    public String generate() {
        final int limit = getLimit();
        int v1 = RandomUtil.randomInt(limit);
        int v2 = RandomUtil.randomInt(limit);
        String max = Integer.toString(Math.max(v1, v2));
        String min = Integer.toString(Math.min(v1, v2));
        // 不够位数的填充空格
        max = StringUtils.rightPad(max, this.numberLength, CharUtil.SPACE);
        min = StringUtils.rightPad(min, this.numberLength, CharUtil.SPACE);
        return max + RandomUtil.randomChar(SEPARATOR) + min + "=";
    }

    @Override
    public boolean verify(String code, String userInputCode) {
        int result;
        try {
            result = Integer.parseInt(userInputCode);
        } catch (NumberFormatException e) {
            // 用户输入非数字
            return false;
        }
        final int calculateResult = (int) Calculator.conversion(code);
        return result == calculateResult;
    }

    /**
     * 根据长度获取参与计算数字最大值
     *
     * @return 最大值
     */
    private int getLimit() {
        return Integer.parseInt("1" + StringUtils.repeat('0', this.numberLength));
    }
}
