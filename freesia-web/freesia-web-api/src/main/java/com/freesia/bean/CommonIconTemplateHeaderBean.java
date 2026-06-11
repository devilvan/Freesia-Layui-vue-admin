package com.freesia.bean;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 服务间数据传输对象
 * @date 2026-06-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通用图标模板头表 服务间数据传输对象")
public class CommonIconTemplateHeaderBean extends BaseDto {
    @Schema(description = "模板名称")
    private String name;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "所属用户ID")
    private Long userId;
    @Schema(description = "默认标识")
    private Boolean defaultFlag;
}
