package com.freesia.config;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import com.freesia.properties.CaptchaProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.awt.*;

/**
 * @author Evad.Wu
 * @Description 验证码 配置类
 * @date 2023-11-04
 */
@AutoConfiguration
@EnableConfigurationProperties(CaptchaProperties.class)
public class CaptchaConfig {

    private static final int WIDTH = 160;
    private static final int HEIGHT = 60;
    private static final Color BACKGROUND = Color.PINK;
    private static final Font FONT = new Font("Consolas", Font.BOLD, 48);

    /**
     * 圆圈干扰验证码
     */
    @Bean
    @ConditionalOnProperty(prefix = "captcha", name = "strategy", havingValue = "CIRCLE")
    public CircleCaptcha circleCaptcha() {
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(WIDTH, HEIGHT);
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }

    /**
     * 线段干扰的验证码
     */
    @Bean
    @ConditionalOnProperty(prefix = "captcha", name = "strategy", havingValue = "LINE")
    public LineCaptcha lineCaptcha() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(WIDTH, HEIGHT);
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }

    /**
     * 扭曲干扰验证码
     */
    @Bean
    @ConditionalOnProperty(prefix = "captcha", name = "strategy", havingValue = "SHEAR")
    public ShearCaptcha shearCaptcha() {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(WIDTH, HEIGHT);
        captcha.setBackground(BACKGROUND);
        captcha.setFont(FONT);
        return captcha;
    }
}
