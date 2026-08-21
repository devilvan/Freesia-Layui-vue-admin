package com.freesia.vo;

import com.freesia.constant.AdminConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 邮箱注册请求
 */
@Data
@Schema(description = "邮箱注册请求")
public class EmailRegisterVo {
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{" + AdminConstant.PASSWORD_MIN_LENGTH + "," + AdminConstant.PASSWORD_MAX_LENGTH + "}$";

    @Schema(description = "邮箱")
    @NotBlank(message = "email.invalid")
    @Email(message = "email.invalid")
    private String email;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "密码")
    @NotBlank(message = "user.password.not.null")
    @Length(min = AdminConstant.PASSWORD_MIN_LENGTH, max = AdminConstant.PASSWORD_MAX_LENGTH, message = "user.password.length.invalid.format")
    @Pattern(regexp = PASSWORD_PATTERN, message = "user.password.complex.invalid")
    private String password;

    @Schema(description = "邮箱验证码")
    @NotBlank(message = "email.code.not.null")
    private String code;
}
