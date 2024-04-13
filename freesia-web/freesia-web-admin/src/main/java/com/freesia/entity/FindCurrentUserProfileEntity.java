package com.freesia.entity;

import com.freesia.annotation.Desensitize;
import com.freesia.constant.DesensitizedType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 查询当前用户信息 持久层传输类
 * @date 2024-04-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindCurrentUserProfileEntity extends BaseEntity {
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
    private String email;
    @Schema(description = "手机号码")
    private String telNo;
    @Schema(description = "用户性别（M-男 F-女 U-未知）")
    private String gender;
    @Schema(description = "头像地址")
    private String avatar;
}
