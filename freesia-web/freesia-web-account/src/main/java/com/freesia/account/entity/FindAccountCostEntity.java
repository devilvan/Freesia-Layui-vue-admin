package com.freesia.account.entity;

import com.freesia.account.dto.AccountCostDto;
import com.freesia.po.SysUserPo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 条件查询开销表 结果集
 * @date 2025-02-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindAccountCostEntity extends AccountCostDto {
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
     * 图标URL
     */
    @Schema(description = "图标URL")
    private String url;

}
