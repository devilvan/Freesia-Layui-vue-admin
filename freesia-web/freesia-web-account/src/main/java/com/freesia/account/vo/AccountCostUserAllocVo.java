package com.freesia.account.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 费用分摊表 值对象
 * @date 2025-10-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "费用分摊表 值对象")
public class AccountCostUserAllocVo extends BaseVo {
    @Schema(description = "记账ID")
    @JsonAlias(value = {"accountId"})
    private Long costId;
    @Schema(description = "用户ID")
    @JsonAlias(value = {"userId"})
    private Long userId;
    @Schema(description = "分摊金额")
    @JsonAlias(value = {"amount"})
    private BigDecimal amount;
    @Schema(description = "分摊时间")
    @JsonAlias(value = {"operateTime"})
    private Date operateTime;
    @Schema(description = "是否分摊（0-否，1-是）")
    @JsonAlias(value = {"allocFlag"})
    private Boolean allocFlag;
}
