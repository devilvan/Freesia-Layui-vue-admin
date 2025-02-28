package com.freesia.account.entity;

import com.freesia.account.dto.AccountCostDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 查询开销表分页信息 结果集
 * @date 2025-02-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPageAccountCostEntity extends AccountCostDto {
    /**
     * 记录人名称
     */
    @Schema(description = "记录人名称")
    private String userName;
}
