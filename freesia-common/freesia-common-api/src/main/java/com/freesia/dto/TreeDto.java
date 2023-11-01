package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 树形结构 数据传输类
 * @date 2023-08-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TreeDto<T> extends BaseDto {
    @Schema(description = "父菜单名称")
    private String parentName;
    @Schema(description = "父菜单ID")
    private Long parentId;
    @Schema(description = "子部门")
    private List<T> children;
}
