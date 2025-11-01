package com.freesia.worldclock.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author Evad.Wu
 * @Description 条件查询日出日落时间表信息 实体类
 * @date 2025-11-01
 */
@Data
public class FindCitySunriseSunsetEntity {
    @Schema(description = "城市ID")
    private Long cityId;
    @Schema(description = "城市名称")
    private String cityName;
    @Schema(description = "城市所属时区")
    private String timezone;
    @Schema(description = "日出日落ID")
    private Long sunriseSunsetId;
    @Schema(description = "日期")
    private LocalDate date;
    @Schema(description = "日出时间（UTC）")
    private LocalTime sunriseTime;
    @Schema(description = "日落时间（UTC）")
    private LocalTime sunsetTime;
    @Schema(description = "日出时间（本地）")
    private LocalTime sunriseTimeLocal;
    @Schema(description = "日落时间（本地）")
    private LocalTime sunsetTimeLocal;
    @Schema(description = "日长")
    private Integer dayLengthMinutes;

}
