package com.freesia.account.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 开销表 值对象
 * @date 2024-12-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "开销表 值对象")
public class AccountCostVo {
    @Schema(description = "开销描述")
    @JSONField(alternateNames = {"desc"})
    private String desc;
    @Schema(description = "开销金额")
    @JSONField(alternateNames = {"amount"})
    private BigDecimal amount;
    @Schema(description = "开销类型（ACCOUNT_COST_TYPE）")
    @JSONField(alternateNames = {"type"})
    private String type;
    @Schema(description = "开销标识（支出、收入）")
    @JSONField(alternateNames = {"paymentSign"})
    private String paymentSign;
    @Schema(description = "时间")
    @JSONField(alternateNames = {"paymentTime"})
    private Date paymentTime;
    @Schema(description = "图标")
    @JSONField(alternateNames = {"icon"})
    private String icon;
    @Schema(description = "备注")
    @JSONField(alternateNames = {"remark"})
    private String remark;
}
