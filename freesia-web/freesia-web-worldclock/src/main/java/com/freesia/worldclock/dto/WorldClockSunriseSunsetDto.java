package com.freesia.worldclock.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private LocalDate date;
    @Schema(description = "日出时间")
    private LocalTime sunriseTime;
    @Schema(description = "日落时间")
    private LocalTime sunsetTime;
    @Schema(description = "日长时间（分钟）")
    private Integer dayLengthMinutes;

    public WorldClockSunriseSunsetDto(Long cityId, LocalDate date, LocalTime sunriseTime, LocalTime sunsetTime) {
        this.cityId = cityId;
        this.date = date;
        this.sunriseTime = sunriseTime;
        this.sunsetTime = sunsetTime;
        this.calculateDayLength();
    }

    private void calculateDayLength() {
        if (sunriseTime != null && sunsetTime != null) {
            long minutes = java.time.Duration.between(sunriseTime, sunsetTime).toMinutes();
            this.dayLengthMinutes = (int) minutes;
        }
    }
}
