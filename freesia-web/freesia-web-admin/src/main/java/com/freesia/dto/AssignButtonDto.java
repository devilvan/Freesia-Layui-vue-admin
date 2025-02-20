package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 分配按钮 数据传输对象
 * @date 2024-02-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分配按钮 数据传输对象")
public class AssignButtonDto {
    @Schema(description = "角色ID")
    private String roleId;
    @Schema(description = "分配前的按钮ID")
    private List<String> beforeAssignButtonIdList;
    @Schema(description = "分配后的按钮ID")
    private List<String> assignButtonIdList;
}
