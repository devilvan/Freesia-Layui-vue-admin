package com.freesia.entity;

import com.freesia.annotation.Phone_CN;
import com.freesia.constant.AdminConstant;
import com.freesia.excel.pojo.BaseImportEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * @author Evad.Wu
 * @Description 用户导入 传输实体
 * {@link com.freesia.controller.SysUserController#userImport}
 * @date 2024-03-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserImportEntity extends BaseImportEntity {
    @NotBlank(message = "{user.username.not.null}")
    @Length(min = AdminConstant.USERNAME_MIN_LENGTH, max = AdminConstant.USERNAME_MAX_LENGTH, message = "{user.username.length.invalid}")
    @Schema(description = "用户名")
    private String userName;
    @NotEmpty(message = "{not.null}")
    @Schema(description = "昵称")
    private String nickName;
    @NotBlank(message = "{user.password.not.null}")
    @Length(min = AdminConstant.PASSWORD_MIN_LENGTH, max = AdminConstant.PASSWORD_MAX_LENGTH, message = "{user.password.length.invalid}")
    @Schema(description = "密码")
    private String password;
    @NotEmpty(message = "{not.null}")
    @Email(message = "{email.invalid}")
    @Schema(description = "邮箱")
    private String email;
    @NotEmpty(message = "not.null")
    @Phone_CN(message = "phone_CN_invalid")
    @Schema(description = "手机号")
    private String telNo;
    @Schema(description = "用户类型")
    private String userType;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "备注")
    private String remark;
}
