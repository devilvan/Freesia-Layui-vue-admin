package com.freesia.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.constant.BudgetType;
import com.freesia.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description Echarts容量图 实体类
 * @date 2025-03-05
 */
@Data
public class EchartCapacityOptionEntity {
    @Schema(description = "主键")
    private Long id;
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
    @Schema(description = "所属账本")
    private String tenantName;
    @Schema(description = "攒钱")
    private BigDecimal saveUp;

    /**
     * 根据预算类型构建时间范围
     *
     * @param budgetType 预算类型
     */
    public void buildDuration(String budgetType) {
        if (BudgetType.DAY.getCode().equals(budgetType)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            this.setDurationFrom(calendar.getTime());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            this.setDurationTo(calendar.getTime());
        } else if (BudgetType.WEEK.getCode().equals(budgetType)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -(calendar.get(Calendar.DAY_OF_WEEK) - 2) % 7);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            this.setDurationFrom(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 6);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            this.setDurationTo(calendar.getTime());
        } else if (BudgetType.MONTH.getCode().equals(budgetType)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            this.setDurationFrom(calendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            this.setDurationTo(calendar.getTime());
        } else if (BudgetType.YEAR.getCode().equals(budgetType)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            this.setDurationFrom(calendar.getTime());
            calendar.set(Calendar.MONTH, 11);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            this.setDurationTo(calendar.getTime());
        }
    }
}
