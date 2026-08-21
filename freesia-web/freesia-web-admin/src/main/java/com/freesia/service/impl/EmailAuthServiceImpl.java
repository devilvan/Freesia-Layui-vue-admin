package com.freesia.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.freesia.constant.CacheConstant;
import com.freesia.constant.EmailCodeScene;
import com.freesia.constant.UserModule;
import com.freesia.exception.ServiceException;
import com.freesia.mail.util.UMail;
import com.freesia.redis.util.URedis;
import com.freesia.repository.SysUserRepository;
import com.freesia.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 邮箱验证码服务实现
 */
@Service
@RequiredArgsConstructor
public class EmailAuthServiceImpl implements EmailAuthService {
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private final SysUserRepository sysUserRepository;

    @Override
    public void sendEmailCode(String email, EmailCodeScene scene) {
        String normalizedEmail = validateEmail(email);
        EmailCodeScene validScene = validateScene(scene);
        boolean exist = ObjectUtil.isNotNull(sysUserRepository.findByEmailAndLogicDel(normalizedEmail, false));
        if (validScene == EmailCodeScene.REGISTER && exist) {
            throw new ServiceException(UserModule.SubModule.REGISTER, "user.email.registered", new Object[]{normalizedEmail});
        }
        if (validScene == EmailCodeScene.RESET_PASSWORD && !exist) {
            throw new ServiceException(UserModule.SubModule.REGISTER, "user.email.not.exists", new Object[]{normalizedEmail});
        }
        String code = RandomUtil.randomNumbers(6);
        String redisKey = buildRedisKey(validScene, normalizedEmail);
        URedis.set(redisKey, code, CODE_TTL);
        try {
            String subject = validScene == EmailCodeScene.REGISTER ? "Freesia 注册验证码" : "Freesia 找回密码验证码";
            String content = buildMailContent(validScene, code);
            UMail.sendHtml(normalizedEmail, subject, content);
        } catch (Exception e) {
            URedis.delete(redisKey);
            throw new ServiceException(UserModule.SubModule.REGISTER, "email.send.failed");
        }
    }

    @Override
    public void validateEmailCode(String email, String code, EmailCodeScene scene) {
        String normalizedEmail = validateEmail(email);
        EmailCodeScene validScene = validateScene(scene);
        String redisKey = buildRedisKey(validScene, normalizedEmail);
        String cacheCode = URedis.get(redisKey);
        if (StrUtil.isBlank(cacheCode)) {
            throw new ServiceException(UserModule.SubModule.REGISTER, "email.code.expired");
        }
        if (!StrUtil.equalsIgnoreCase(cacheCode, code)) {
            throw new ServiceException(UserModule.SubModule.REGISTER, "email.code.invalid");
        }
        URedis.delete(redisKey);
    }

    private String buildMailContent(EmailCodeScene scene, String code) {
        String title = scene == EmailCodeScene.REGISTER ? "注册" : "重置密码";
        return """
                <div style="font-family:Arial,sans-serif;line-height:1.8;color:#1f2937">
                  <p>您好，您正在进行 <b>%s</b> 操作。</p>
                  <p>邮箱验证码如下：</p>
                  <p style="font-size:28px;font-weight:700;letter-spacing:6px;color:#009688">%s</p>
                  <p>验证码 5 分钟内有效，请勿泄露给他人。</p>
                </div>
                """.formatted(title, code);
    }

    private String buildRedisKey(EmailCodeScene scene, String email) {
        return CacheConstant.EMAIL_CODE_KEY + scene.getCode() + ":" + email;
    }

    private String validateEmail(String email) {
        if (StrUtil.isBlank(email) || !Validator.isEmail(email)) {
            throw new ServiceException(UserModule.SubModule.REGISTER, "email.invalid");
        }
        return email.trim();
    }

    private EmailCodeScene validateScene(EmailCodeScene scene) {
        if (scene == null) {
            throw new ServiceException(UserModule.SubModule.REGISTER, "email.code.scene.invalid");
        }
        return scene;
    }
}
