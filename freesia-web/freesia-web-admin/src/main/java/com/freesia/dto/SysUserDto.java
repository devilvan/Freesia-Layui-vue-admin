package com.freesia.dto;

import com.freesia.desensization.annotation.Desensitize;
import com.freesia.desensization.constant.DesensitizedType;
import com.freesia.oss.annotation.Domain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 用户信息表 数据传输对象
 * @date 2023-08-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户信息表 数据传输对象")
public class SysUserDto extends BaseDto {
    @Schema(description = "部门ID")
    private Long deptId;
    @Schema(description = "用户账号")
    private String userName;
    @Schema(description = "用户昵称")
    private String nickName;
    @Schema(description = "用户类型（见USER_TYPE）")
    private String userType;
    @Schema(description = "用户邮箱")
    @Desensitize(strategy = DesensitizedType.EMAIL)
    private String email;
    @Schema(description = "手机号码")
    private String telNo;
    @Schema(description = "用户性别（M-男 F-女 U-未知）")
    private String gender;
    @Domain
    @Schema(description = "头像地址")
    private String avatar;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "帐号状态（0-否，1-是）")
    private String accountStatus;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间（从）")
    private Date createTimeFrom;
    @Schema(description = "创建时间（从）")
    private Date createTimeTo;
}
