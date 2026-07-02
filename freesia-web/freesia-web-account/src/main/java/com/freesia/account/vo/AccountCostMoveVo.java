package com.freesia.account.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 移到账本 值对象
 * @date 2025-07-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "移到账本 值对象")
public class AccountCostMoveVo {
    @Schema(description = "记账ID集合")
    private List<Long> idList;
    @Schema(description = "目标租户ID")
    private Long targetTenantId;
}
