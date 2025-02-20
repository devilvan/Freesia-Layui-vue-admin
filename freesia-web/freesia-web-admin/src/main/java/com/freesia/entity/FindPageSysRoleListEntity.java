package com.freesia.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.constant.Constants;
import com.freesia.mapper.SysRoleMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description {@link SysRoleMapper#findPageSysRoleList} 持久层传输对象
 * @date 2023-09-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPageSysRoleListEntity extends BaseEntity {
    @Schema(description = "角色ID")
    private Long id;
    @Schema(description = "角色名称")
    private String roleName;
    @Schema(description = "角色权限字符串")
    private String roleKey;
    @Schema(description = "角色状态（0-停用，1-正常）")
    private String status;
    @Schema(description = "显示顺序")
    private Integer orderNum;
    @Schema(description = "数据范围（见DATA_SCOPE）")
    private String dataScope;
    @Schema(description = "菜单树选择项是否关联显示")
    private Boolean menuCheckStrictly;
    @Schema(description = "部门树选择项是否关联显示")
    private Boolean deptCheckStrictly;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "逻辑删除")
    private Boolean logicDel;
    @Schema(description = "创建时间")
    @JSONField(format = Constants.YMD_HMS)
    private Date createTime;
}
