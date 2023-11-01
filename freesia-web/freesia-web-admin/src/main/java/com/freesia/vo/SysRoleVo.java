package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 角色信息表 值对象
 * @date 2023-08-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "角色信息表 值对象")
public class SysRoleVo {
    @Schema(description = "角色名称")
    @JSONField(alternateNames = {"roleName"})
    private String roleName;
    @Schema(description = "角色权限字符串")
    @JSONField(alternateNames = {"roleKey"})
    private String roleKey;
    @Schema(description = "角色状态（0正常 1停用）")
    @JSONField(alternateNames = {"status"})
    private String status;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
    @Schema(description = "创建时间（从）")
    private Date createTimeFrom;
    @Schema(description = "创建时间（从）")
    private Date createTimeTo;
}
