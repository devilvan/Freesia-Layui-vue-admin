package com.freesia.entity;

import com.freesia.dto.TreeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 查询菜单树下拉框 持久层传输类
 * @date 2023-10-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindTreeMenuSelectEntity extends TreeDto<FindTreeMenuSelectEntity> {
    @Schema(description = "菜单名称")
    private String title;
    @Schema(description = "菜单类型")
    private String menuType;
    @Schema(description = "选项值")
    private Long field;
}
