package com.freesia.properties;

import com.freesia.constant.CaptchaInterferenceStrategy;
import com.freesia.constant.CaptchaType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Evad.Wu
 * @Description 登录验证码 属性类
 * @date 2023-11-04
 */
@Data
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {
    @Schema(description = "验证码图片类型")
    private CaptchaType type;
    /**
     * line 线段干扰
     * circle 圆圈干扰
     * shear 扭曲干扰
     */
    @Schema(description = "验证码干扰策略")
    private CaptchaInterferenceStrategy strategy;
    @Schema(description = "数字验证码长度")
    private int numberLength;
    @Schema(description = "字符验证码长度")
    private int charLength;


}
