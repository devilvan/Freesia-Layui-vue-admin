package com.freesia.worldclock.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 城市表 数据传输对象
 * @date 2025-10-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "城市表 数据传输对象")
public class WorldClockCityDto extends BaseDto {
    @Schema(description = "城市名称")
    private String cityName;
    @Schema(description = "国家编码")
    private String countryCode;
    @Schema(description = "时区")
    private String timezone;
    @Schema(description = "纬度")
    private BigDecimal latitude;
    @Schema(description = "经度")
    private BigDecimal longitude;
    @Schema(description = "城市名称集合")
    private List<String> cityNameList;
}
