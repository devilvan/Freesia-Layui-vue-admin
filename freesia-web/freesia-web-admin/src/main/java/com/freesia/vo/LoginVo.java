package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.constant.AdminConstant;
import com.freesia.controller.SysLoginController;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author Evad.Wu
 * @Description 登录功能 值对象传递 {@link SysLoginController#sysLogin}
 * @date 2023-08-11
 */
@Data
public class LoginVo {
    @Schema(description = "用户名")
    @NotBlank(message = "{user.username.not.null}")
    @Length(min = AdminConstant.USERNAME_MIN_LENGTH, max = AdminConstant.USERNAME_MAX_LENGTH, message = "{user.username.length.invalid}")
    @JSONField(alternateNames = {"username", "account"})
    private String username;
    @Schema(description = "用户密码")
    @NotBlank(message = "{user.password.not.null}")
    @Length(min = AdminConstant.PASSWORD_MIN_LENGTH, max = AdminConstant.PASSWORD_MAX_LENGTH, message = "{user.password.length.valid}")
    private String password;
    @Schema(description = "验证码")
    private String code;
    @Schema(description = "唯一标识")
    private String captchaKey;
}
