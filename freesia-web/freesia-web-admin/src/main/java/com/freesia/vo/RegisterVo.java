package com.freesia.vo;

import com.freesia.constant.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 注册功能 值对象
 * @date 2023-08-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "注册功能 值对象")
public class RegisterVo extends LoginVo {
    /**
     * {@link UserType}
     */
    @Schema(description = "用户类型")
    private String userType;
}
