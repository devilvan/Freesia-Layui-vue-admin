package com.freesia.account.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.account.dto.AccountCostUserAllocDto;
import com.freesia.constant.Constants;
import com.freesia.dto.BaseDto;
import com.freesia.dto.SysTenantDto;
import com.freesia.dto.SysUserDto;
import com.freesia.oss.annotation.Domain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 查询开销表分页信息 结果集
 * @date 2025-02-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPageAccountCostEntity extends BaseDto {
    @Schema(description = "开销描述")
    private String costDesc;
    @Schema(description = "开销金额")
    private BigDecimal outlay;
    @Schema(description = "开销类型（ACCOUNT_COST_TYPE）")
    private String costType;
    @Schema(description = "开销标识（支出、收入）")
    private String paymentSign;
    @Schema(description = "时间")
    @JsonFormat(pattern = Constants.YMD_HM)
    private Date paymentTime;
    @Domain
    @Schema(description = "图标")
    private String icon;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "记录人实体")
    private SysUserDto sysUserDto;
    @Schema(description = "费用分摊集合")
    @TableField(exist = false)
    private List<AccountCostUserAllocDto> accountCostUserAllocDtoList;
    @Schema(description = "所属租户")
    private SysTenantDto sysTenantDto;
}
