package com.freesia.account.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.constant.Constants;
import com.freesia.excel.pojo.BaseExportEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 开销表-导入 实体类
 * @date 2025-01-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "开销表-导出 实体类")
public class
AccountCostExportEntity extends BaseExportEntity {
    @Schema(description = "开销描述")
    @ExcelProperty(value = "开销描述")
    private String costDesc;
    @Schema(description = "开销金额")
    @ExcelProperty(value = "开销金额")
    private BigDecimal outlay;
    @Schema(description = "开销类型（ACCOUNT_COST_TYPE）")
    @ExcelProperty(value = "开销类型")
    private String costType;
    @Schema(description = "开销标识（支出、收入）")
    @ExcelIgnore
    private String paymentSign;
    @Schema(description = "开销标识（支出、收入）")
    @ExcelProperty(value = "开销标识")
    private String paymentSignName;
    @Schema(description = "时间")
    @ExcelProperty(value = "时间")
    @JSONField(format = Constants.YMD)
    private Date paymentTime;
    @Schema(description = "备注")
    @ExcelProperty(value = "备注")
    private String remark;
    @Schema(description = "导出日期键")
    @ExcelIgnore
    private String paymentTimeGroupingKey;
    @Schema(description = "总计")
    @ExcelProperty(value = "总计")
    private String statistic;
}
