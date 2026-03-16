package com.freesia.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 系统列头表 数据传输对象
 * @date 2026-03-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统列头表 数据传输对象")
public class SysColumnHeaderDto extends BaseDto {
    @Schema(description = "组件名")
    private String component;
    @Schema(description = "是否启用（true-是；false-否）")
    private Boolean enabled;
}
