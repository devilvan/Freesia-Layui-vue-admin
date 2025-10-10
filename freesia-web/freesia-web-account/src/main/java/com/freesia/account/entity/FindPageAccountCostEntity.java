package com.freesia.account.entity;

import com.freesia.account.dto.AccountCostDto;
import com.freesia.po.SysUserPo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 查询开销表分页信息 结果集
 * @date 2025-02-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPageAccountCostEntity extends AccountCostDto {
    /**
     * 关联用户集合
     */
    @Schema(description = "关联用户集合")
    private List<SysUserPo> userList;
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
