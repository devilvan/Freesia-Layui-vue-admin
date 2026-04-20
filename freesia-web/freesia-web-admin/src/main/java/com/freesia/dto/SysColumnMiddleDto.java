package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 系统列中间表 数据传输对象
 * @date 2026-03-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统列中间表 数据传输对象")
public class SysColumnMiddleDto extends BaseDto {
    @Schema(description = "系统列头ID")
    private Long headerId;
    @Schema(description = "列名")
    private String title;
    @Schema(description = "属性名")
    private String name;
    @Schema(description = "是否可用（true-是；false-否）")
    private Boolean enabled;
    @Schema(description = "自定义插槽")
    private String customSlot;
}
