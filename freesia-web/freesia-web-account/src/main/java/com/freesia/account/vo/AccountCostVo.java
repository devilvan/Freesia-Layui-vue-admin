package com.freesia.account.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 开销表 值对象
 * @date 2024-12-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "开销表 值对象")
public class AccountCostVo extends BaseVo {
    @Schema(description = "开销描述")
    @JsonAlias(value = {"costDesc"})
    private String costDesc;
    @Schema(description = "开销金额")
    @JsonAlias(value = {"outlay"})
    private BigDecimal outlay;
    @Schema(description = "开销类型（ACCOUNT_COST_TYPE）")
    @JsonAlias(value = {"costType"})
    private String costType;
    @Schema(description = "开销标识（支出、收入）")
    @JsonAlias(value = {"paymentSign"})
    private String paymentSign;
    @Schema(description = "时间")
    @JsonAlias(value = {"paymentTime"})
    private Date paymentTime;
    @Schema(description = "时间范围（查询）")
    @JsonAlias(value = {"paymentTimeRange"})
    private String paymentTimeRange;
    @Schema(description = "图标")
    @JsonAlias(value = {"icon"})
    private String icon;
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
    private String remark;
    @Schema(description = "关联用户ID集合")
    @JsonAlias(value = {"accountCostUserIdList"})
    private List<Long> accountCostUserIdList;
    @Schema(description = "是否统计所有账本")
    @JsonAlias(value = {"allTenantFlag"})
    private Boolean allTenantFlag = false;
    @Schema(description = "开销类型")
    @JsonAlias(value = {"costTypeList"})
    private List<String> costTypeList;
    @Schema(description = "费用分摊数据")
    @JsonAlias(value = {"accountCostUserAllocVoList"})
    private List<AccountCostUserAllocVo> accountCostUserAllocVoList;
}
