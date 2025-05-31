package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 用户信息表 值对象
 * @date 2023-08-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户信息表 值对象")
public class SysUserVo extends BaseVo {
    @Schema(description = "部门ID")
    @JsonAlias(value = {"deptId"})
    private Long deptId;
    @Schema(description = "用户账号")
    @JsonAlias(value = {"userName"})
    private String userName;
    @Schema(description = "用户昵称")
    @JsonAlias(value = {"nickName"})
    private String nickName;
    @Schema(description = "用户类型（见USER_TYPE）")
    @JsonAlias(value = {"userType"})
    private String userType;
    @Schema(description = "用户邮箱")
    @JsonAlias(value = {"email"})
    private String email;
    @Schema(description = "手机号码")
    @JsonAlias(value = {"telNo"})
    private String telNo;
    @Schema(description = "用户性别（M-男 F-女 U-未知）")
    @JsonAlias(value = {"gender"})
    private String gender;
    @Schema(description = "头像地址")
    @JsonAlias(value = {"avatar"})
    private String avatar;
    @Schema(description = "帐号状态（0-否，1-是）")
    @JsonAlias(value = {"accountStatus"})
    private String accountStatus;
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
    @Schema(description = "创建时间（从）")
    @JsonFormat(pattern = Constants.YMD_HMS)
    private Date createTimeFrom;
    @Schema(description = "创建时间（从）")
    @JsonFormat(pattern = Constants.YMD_HMS)
    private Date createTimeTo;

}
