package com.freesia.worldclock.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 城市表 值对象
 * @date 2025-10-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "城市表 值对象")
public class WorldClockCityVo extends BaseVo {
    @Schema(description = "城市名称")
    @JsonAlias(value = {"cityName"})
    private String cityName;
    @Schema(description = "国家编码")
    @JsonAlias(value = {"countryCode"})
    private String countryCode;
    @Schema(description = "时区")
    @JsonAlias(value = {"timezone"})
    private String timezone;
    @Schema(description = "纬度")
    @JsonAlias(value = {"latitude"})
    private BigDecimal latitude;
    @Schema(description = "经度")
    @JsonAlias(value = {"longitude"})
    private BigDecimal longitude;
    @Schema(description = "城市名称集合")
    private List<String> cityNameList;
}
