package com.freesia.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.constant.Constants;
import com.freesia.desensization.annotation.Desensitize;
import com.freesia.desensization.constant.DesensitizedType;
import com.freesia.oss.annotation.Domain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 获取用户列表分页 持久层传输对象
 * {@link com.freesia.controller.SysUserController#findPageSysUserList}
 * @date 2023-08-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPageSysUserListEntity extends BaseEntity {
    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "部门ID")
    private Long deptId;
    @Schema(description = "用户昵称")
    private String nickName;
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "邮箱")
    @Desensitize(strategy = DesensitizedType.EMAIL)
    private String email;
    @Domain
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "电话号码")
    @Desensitize(strategy = DesensitizedType.MOBILE_PHONE)
    private String telNo;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "用户状态")
    private String accountStatus;
    @Schema(description = "逻辑删除")
    private Boolean logicDel;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = Constants.YMD_HMS)
    private Date createTime;
    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "部门名称")
    private String deptName;
    @Schema(description = "部门负责人")
    private String leader;
}
