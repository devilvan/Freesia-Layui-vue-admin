package com.freesia.account.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.constant.Constants;
import com.freesia.excel.pojo.BaseImportEntity;
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
@Schema(description = "开销表-导入 实体类")
public class AccountCostImportEntity extends BaseImportEntity {
    @Schema(description = "开销描述")
    private String costDesc;
    @Schema(description = "开销金额")
    private BigDecimal outlay;
    @Schema(description = "开销类型（ACCOUNT_COST_TYPE）")
    private String costType;
    @Schema(description = "开销标识（支出、收入）")
    private String paymentSign;
    @Schema(description = "时间")
    @JSONField(format = Constants.YMD)
    private Date paymentTime;
    @Schema(description = "备注")
    private String remark;
}
