package com.freesia.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 保存角色 值对象
 * @date 2024-07-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "保存角色 值对象")
public class SaveRoleVo extends BaseVo {
    @Schema(description = "角色名称")
    @JSONField(alternateNames = {"roleName"})
    private String roleName;
    @Schema(description = "角色权限字符串")
    @JSONField(alternateNames = {"roleKey"})
    private String roleKey;
    @Schema(description = "角色状态（0-停用，1-正常）")
    @JSONField(alternateNames = {"status"})
    private String status;
    @Schema(description = "数据范围")
    @JSONField(alternateNames = {"dataScope"})
    private String dataScope;
    @Schema(description = "排序号")
    @JSONField(alternateNames = {"orderNum"})
    private Integer orderNum;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
    @Schema(description = "创建时间（从）")
    private Date createTimeFrom;
    @Schema(description = "创建时间（从）")
    private Date createTimeTo;
}
