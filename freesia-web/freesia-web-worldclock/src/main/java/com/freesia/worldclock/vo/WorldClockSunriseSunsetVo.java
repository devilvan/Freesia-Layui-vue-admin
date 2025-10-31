package com.freesia.worldclock.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 日出日落时间表 值对象
 * @date 2025-10-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "日出日落时间表 值对象")
public class WorldClockSunriseSunsetVo extends BaseVo {
    @Schema(description = "城市ID")
    @JsonAlias(value = {"cityId"})
    private Long cityId;
    @Schema(description = "日期")
    @JsonAlias(value = {"date"})
    private Date date;
    @Schema(description = "日出时间")
    @JsonAlias(value = {"sunriseTime"})
    private Date sunriseTime;
    @Schema(description = "日落时间")
    @JsonAlias(value = {"sunsetTime"})
    private Date sunsetTime;
    @Schema(description = "日长时间（分钟）")
    @JsonAlias(value = {"dayLengthMinutes"})
    private Integer dayLengthMinutes;
}
