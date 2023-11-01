package com.freesia.dto;

import com.freesia.constant.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Evad.Wu
 * @Description 注册功能 值对象
 * @date 2023-08-22
 */
@Data
@Schema(description = "注册功能 数据传输对象")
public class RegisterDto {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "用户密码")
    private String password;
    @Schema(description = "验证码")
    private String code;
    @Schema(description = "唯一标识")
    private String uuid;
    /**
     * {@link UserType}
     */
    @Schema(description = "用户类型")
    private String userType;
}
