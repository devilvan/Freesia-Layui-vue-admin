package com.freesia.vo;

import com.freesia.controller.SysMenuController;
import com.freesia.dto.AssignButtonDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 分配按钮 值对象
 * {@link SysMenuController#assignButton}
 * @date 2024-02-19
 */
@Data
@Schema(description = "分配按钮 值对象")
public class AssignButtonVo {
    @Schema(description = "角色ID")
    @NotEmpty(message = "{not.null}")
    private String roleId;
    @Schema(description = "分配前的按钮ID")
    private List<String> beforeAssignButtonIdList;
    @Schema(description = "分配后的按钮ID")
    private List<String> assignButtonIdList;

    public static AssignButtonDto convertVo2Dto(AssignButtonVo assignButtonVo) {
        return new AssignButtonDto(assignButtonVo.getRoleId(), assignButtonVo.getBeforeAssignButtonIdList(), assignButtonVo.getAssignButtonIdList());
    }
}
