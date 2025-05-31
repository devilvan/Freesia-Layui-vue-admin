package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色信息表 值对象")
public class SysRoleVo extends BaseVo {
    @Schema(description = "角色名称")
    @JsonAlias(value = {"roleName"})
    private String roleName;
    @Schema(description = "角色权限字符串")
    @JsonAlias(value = {"roleKey"})
    private String roleKey;
    @Schema(description = "角色状态（0-停用，1-正常）")
    @JsonAlias(value = {"status"})
    private String status;
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
    @Schema(description = "创建时间（从）")
    private Date createTimeFrom;
    @Schema(description = "创建时间（从）")
    private Date createTimeTo;
}
