package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.CaptchaType;
import com.freesia.constant.Constants;
import com.freesia.constant.SysConfigConstant;
import com.freesia.dto.CaptchaCodeDto;
import com.freesia.dto.SysConfigDto;
import com.freesia.po.SysConfigPo;
import com.freesia.properties.CaptchaProperties;
import com.freesia.service.SysConfigService;
import com.freesia.redis.util.URedis;
import com.freesia.util.UReflect;
import com.freesia.util.USpring;
import com.freesia.vo.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * @author Evad.Wu
 * @Description 登录验证码 控制类
 * @date 2023-11-02
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/captchaController")
public class CaptchaController extends BaseController {
    private final CaptchaProperties captchaProperties;
    private final SysConfigService sysConfigService;

    @SaIgnore
    @GetMapping("getCaptchaCode")
    public R<CaptchaCodeDto> getCaptchaCode() {
        CaptchaCodeDto captchaCodeDto = new CaptchaCodeDto();
        SysConfigDto sysConfigDto = sysConfigService.findConfigByKey(SysConfigConstant.SYS_ACCOUNT_CAPTCHA_ENABLED);
        String configValue = sysConfigDto.getConfigValue();
        boolean captchaEnabled = Boolean.parseBoolean(configValue);
        captchaCodeDto.setEnabled(captchaEnabled);
        if (!captchaEnabled) {
            return R.ok(captchaCodeDto);
        }
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        String verifyKey = CacheConstant.CAPTCHA_CODE_KEY + uuid;
        // 生成验证码
        CaptchaType captchaType = captchaProperties.getType();
        boolean isMath = CaptchaType.CALCULATE == captchaType;
        Integer length = isMath ? captchaProperties.getNumberLength() : captchaProperties.getCharLength();
        CodeGenerator codeGenerator = UReflect.newInstance(captchaType.getType(), length);
        AbstractCaptcha captcha = USpring.getBean(captchaProperties.getStrategy().getStrategy());
        captcha.setGenerator(codeGenerator);
        captcha.createCode();
        String code = captcha.getCode();
        if (isMath) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(StringUtils.remove(code, "="));
            code = exp.getValue(String.class);
        }
        URedis.set(verifyKey, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));
        captchaCodeDto.setCaptchaKey(uuid);
        captchaCodeDto.setCaptchaImg(captcha.getImageBase64());
        return R.ok(captchaCodeDto);
    }
}
