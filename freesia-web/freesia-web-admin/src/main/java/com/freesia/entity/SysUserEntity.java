package com.freesia.entity;

import com.freesia.desensization.annotation.Desensitize;
import com.freesia.desensization.constant.DesensitizedType;
import com.freesia.controller.SysLoginController;
import com.freesia.oss.annotation.Domain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 登录后获取用户信息 持久层传输类
 * {@link SysLoginController#getInfo()}
 * @date 2023-12-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserEntity extends BaseEntity {
    @Schema(description = "主键ID")
    private Long id;
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
    @Desensitize(strategy = DesensitizedType.MOBILE_PHONE)
    private String telNo;
    @Schema(description = "用户性别（M-男 F-女 U-未知）")
    private String gender;
    @Domain
    @Schema(description = "头像地址")
    private String avatar;
}
