package com.freesia.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description Echarts容量图 实体类
 * @date 2025-03-05
 */
@Data
public class EchartCapacityOptionEntity {
    @Schema(description = "描述")
    private String name;
    @Schema(description = "值（单位：%）")
    private BigDecimal value;
    @Schema(description = "预算金额")
    private BigDecimal budget;
    @Schema(description = "开销金额")
    private BigDecimal outlay;
    @Schema(description = "时间范围从")
    @JsonFormat(pattern = Constants.YMD)
    private Date durationFrom;
    @Schema(description = "时间范围到")
    @JsonFormat(pattern = Constants.YMD)
    private Date durationTo;
    @Schema(description = "预算类型")
    private String budgetType;
}
