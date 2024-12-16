package com.freesia.account.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 开销表 数据传输对象
 * @date 2024-12-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "开销表 数据传输对象")
public class AccountCostDto extends BaseDto {
    @Schema(description = "开销描述")
    private String desc;
    @Schema(description = "开销金额")
    private BigDecimal amount;
    @Schema(description = "开销类型（ACCOUNT_COST_TYPE）")
    private String type;
    @Schema(description = "开销标识（支出、收入）")
    private String paymentSign;
    @Schema(description = "时间")
    private Date paymentTime;
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "备注")
    private String remark;
}
