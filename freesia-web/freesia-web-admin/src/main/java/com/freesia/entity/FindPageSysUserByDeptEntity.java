package com.freesia.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 查询部门下的用户 {@link SysUserMapper#findPageSysUserByDept} 持久层传输对象
 * @date 2023-09-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPageSysUserByDeptEntity extends BaseEntity {
    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "部门ID")
    private Long deptId;
    @Schema(description = "用户昵称")
    private String nickName;
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "电话号码")
    private String telNo;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "用户状态")
    private String accountStatus;
    @Schema(description = "逻辑删除")
    private Boolean logicDel;
    @Schema(description = "创建时间")
    @JSONField(format = Constants.YMD_HMS)
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
