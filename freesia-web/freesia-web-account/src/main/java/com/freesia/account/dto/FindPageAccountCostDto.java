package com.freesia.account.dto;

import com.freesia.account.entity.FindPageAccountCostEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 查询开销表分页信息 数据传输对象
 * @date 2025-10-27
 */
@Data
public class FindPageAccountCostDto extends FindPageAccountCostEntity {
    /**
     * 关联用户ID
     */
    @Schema(description = "关联用户ID")
    private String accountCostUserId;
    /**
     * 关联用户昵称
     */
    @Schema(description = "关联用户昵称")
    private String accountCostUserName;
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String acNickName;
    /**
     * 分摊金额
     */
    @Schema(description = "分摊金额")
    private BigDecimal allocAmount;
    /**
     * 分摊状态
     */
    @Schema(description = "分摊状态")
    private String allocStatus;
}
