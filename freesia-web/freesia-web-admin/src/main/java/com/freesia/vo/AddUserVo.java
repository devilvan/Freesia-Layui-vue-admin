package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.constant.AdminConstant;
import com.freesia.validation.annotation.Phone_CN;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Evad.Wu
 * @Description 新增用户 值对象
 * {@link com.freesia.controller.SysUserController#saveUserInfo}
 * @date 2024-04-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddUserVo extends BaseVo {
    private Long deptId;
    @Schema(description = "用户账号")
    @JSONField(alternateNames = {"userName"})
    @NotEmpty(message = "not.null")
    @Length(min = AdminConstant.USERNAME_MIN_LENGTH, max = AdminConstant.USERNAME_MAX_LENGTH, message = "user.username.length.invalid.format")
    private String userName;
    @Schema(description = "用户密码")
    @NotEmpty(message = "user.password.not.null")
    @Length(min = AdminConstant.PASSWORD_MIN_LENGTH, max = AdminConstant.PASSWORD_MAX_LENGTH, message = "user.password.length.invalid.format")
    private String password;
    @Schema(description = "用户昵称")
    @JSONField(alternateNames = {"nickName"})
    @NotEmpty(message = "not.null")
    private String nickName;
    @Schema(description = "用户邮箱")
    @JSONField(alternateNames = {"email"})
    @NotEmpty(message = "not.null")
    @Email(message = "warn.email.invalid")
    private String email;
    @Schema(description = "手机号码")
    @JSONField(alternateNames = {"telNo"})
    @NotEmpty(message = "not.null")
    @Phone_CN(message = "phone_CN_invalid")
    private String telNo;
    @Schema(description = "用户性别（M-男 F-女 U-未知）")
    @JSONField(alternateNames = {"gender"})
    private String gender;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
    @Schema(description = "数字")
    @JSONField(alternateNames = {"num"})
    @Min(message = "min.exceeded", value = 3)
    private Double num;
    @Valid
    @NotNull
    @Schema(description = "子属性")
    @JSONField(alternateNames = {"sub"})
    private Sub sub;

    @Data
    @Validated
    public static class Sub {
        @Schema(description = "用户账号")
        @JSONField(alternateNames = {"userName"})
        @NotEmpty(message = "not.null")
        @Length(min = AdminConstant.USERNAME_MIN_LENGTH, max = AdminConstant.USERNAME_MAX_LENGTH, message = "user.username.length.invalid.format")
        private String theName;
    }
}
