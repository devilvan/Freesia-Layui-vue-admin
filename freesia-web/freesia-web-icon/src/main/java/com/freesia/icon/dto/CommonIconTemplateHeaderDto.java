package com.freesia.icon.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 数据传输对象
 * @date 2025-04-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通用图标模板头表 数据传输对象")
public class CommonIconTemplateHeaderDto extends BaseDto {
    @Schema(description = "模板名称")
    private String name;
    @Schema(description = "排序")
    private Integer orderNum;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "所属用户ID")
    private Long userId;
}
