package com.freesia.worldclock.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 日出日落时间表 数据传输对象
 * @date 2025-10-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "日出日落时间表 数据传输对象")
public class WorldClockSunriseSunsetDto extends BaseDto {
    @Schema(description = "城市ID")
    private Long cityId;
    @Schema(description = "日期")
    private Date date;
    @Schema(description = "日出时间")
    private Date sunriseTime;
    @Schema(description = "日落时间")
    private Date sunsetTime;
    @Schema(description = "日长时间（分钟）")
    private Integer dayLengthMinutes;
}
