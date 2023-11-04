package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 登录功能 BASE64验证码 数据传输类
 * @date 2023-11-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaCodeDto {
    @Schema(description = "是否开启验证码")
    private boolean enabled;
    @Schema(description = "验证码标识")
    private String captchaKey;
    @Schema(description = "验证码BASE64字符串")
    private String captchaImg;

}
