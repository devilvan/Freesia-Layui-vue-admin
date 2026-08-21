package com.freesia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 邮箱验证码发送请求
 */
@Data
@Schema(description = "邮箱验证码发送请求")
public class SendEmailCodeVo {
    @Schema(description = "邮箱")
    @NotBlank(message = "email.invalid")
    @Email(message = "email.invalid")
    private String email;

    @Schema(description = "场景")
    @NotBlank(message = "email.code.scene.invalid")
    private String scene;
}
