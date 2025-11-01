package com.freesia.worldclock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 条件查询城市日出日落时间表信息 请求数据传输对象
 * @date 2025-11-01
 */
@Data
public class FindCitySunriseSunsetReqDto {
    @Schema(description = "城市ID")
    private Long id;
    @Schema(description = "日期")
    private LocalDate date;
    @Schema(description = "城市名称")
    private List<String> cityNameList;
}
