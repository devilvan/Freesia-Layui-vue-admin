package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.http.HttpStatus;
import com.freesia.constant.FlagConstant;
import com.freesia.constant.EmailCodeScene;
import com.freesia.constant.SysConfigConstant;
import com.freesia.constant.UserModule;
import com.freesia.dto.SysConfigDto;
import com.freesia.dto.SysUserDto;
import com.freesia.crypt.util.UCrypt;
import com.freesia.exception.ServiceException;
import com.freesia.service.SysConfigService;
import com.freesia.service.EmailAuthService;
import com.freesia.service.SysRegisterService;
import com.freesia.util.UMessage;
import com.freesia.vo.R;
import com.freesia.vo.EmailRegisterVo;
import com.freesia.vo.ResetPasswordVo;
import com.freesia.vo.SendEmailCodeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.Objects;

/**
 * @author Evad.Wu
 * @Description 注册功能 控制器
 * @date 2023-08-22
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysRegistryController")
@Tag(name = "SysRegistryController", description = "注册功能 控制器")
public class SysRegistryController extends BaseController {
    private final SysRegisterService sysRegisterService;
    private final SysConfigService sysConfigService;
    private final EmailAuthService emailAuthService;
    private final Validator validator;

    @SaIgnore
    @PostMapping("sendEmailCode")
    @Operation(summary = "发送邮箱验证码")
    public R<Void> sendEmailCode(@Validated @RequestBody String request) {
        SendEmailCodeVo sendEmailCodeVo = validate(UCrypt.aesDecryptJSON(request, SendEmailCodeVo.class));
        EmailCodeScene scene = EmailCodeScene.from(sendEmailCodeVo.getScene());
        if (Objects.isNull(scene)) {
            throw new ServiceException(UserModule.USER_MANAGEMENT, "email.code.scene.invalid");
        }
        if (scene == EmailCodeScene.REGISTER) {
            SysConfigDto sysConfigDto = sysConfigService.findConfigByKey(SysConfigConstant.SYS_ACCOUNT_REGISTER_USER);
            if (!FlagConstant.TRUE.equals(sysConfigDto.getConfigValue())) {
                return R.ok(HttpStatus.HTTP_BAD_REQUEST, UMessage.message("sys.register.disabled"));
            }
        }
        emailAuthService.sendEmailCode(sendEmailCodeVo.getEmail(), scene);
        return R.ok(HttpStatus.HTTP_OK, UMessage.message("user.email.code.sent"));
    }

    @SaIgnore
    @PostMapping("register")
    @Operation(summary = "用户注册功能")
    public R<Void> register(@Validated @RequestBody String request) {
        SysConfigDto sysConfigDto = sysConfigService.findConfigByKey(SysConfigConstant.SYS_ACCOUNT_REGISTER_USER);
        String flag = sysConfigDto.getConfigValue();
        if (!FlagConstant.TRUE.equals(flag)) {
            return R.ok(HttpStatus.HTTP_BAD_REQUEST, UMessage.message("sys.register.disabled"));
        }
        EmailRegisterVo emailRegisterVo = validate(UCrypt.aesDecryptJSON(request, EmailRegisterVo.class));
        emailAuthService.validateEmailCode(emailRegisterVo.getEmail(), emailRegisterVo.getCode(), EmailCodeScene.REGISTER);
        SysUserDto sysUserDto = new SysUserDto();
        sysUserDto.setEmail(emailRegisterVo.getEmail());
        sysUserDto.setUserName(emailRegisterVo.getEmail());
        sysUserDto.setNickName(emailRegisterVo.getNickName());
        sysUserDto.setPassword(emailRegisterVo.getPassword());
        sysRegisterService.register(sysUserDto);
        return R.ok(HttpStatus.HTTP_OK, UMessage.message("user.register.success"));
    }

    @SaIgnore
    @PostMapping("resetPassword")
    @Operation(summary = "邮箱找回密码")
    public R<Void> resetPassword(@Validated @RequestBody String request) {
        ResetPasswordVo resetPasswordVo = validate(UCrypt.aesDecryptJSON(request, ResetPasswordVo.class));
        emailAuthService.validateEmailCode(resetPasswordVo.getEmail(), resetPasswordVo.getCode(), EmailCodeScene.RESET_PASSWORD);
        SysUserDto sysUserDto = new SysUserDto();
        sysUserDto.setEmail(resetPasswordVo.getEmail());
        sysUserDto.setPassword(resetPasswordVo.getPassword());
        sysRegisterService.resetPassword(sysUserDto);
        return R.ok(HttpStatus.HTTP_OK, UMessage.message("user.password.reset.success"));
    }

    private <T> T validate(T target) {
        Set<ConstraintViolation<T>> violations = validator.validate(target);
        if (!violations.isEmpty()) {
            ConstraintViolation<T> violation = violations.iterator().next();
            throw new ServiceException(UserModule.USER_MANAGEMENT, violation.getMessage());
        }
        return target;
    }
}
