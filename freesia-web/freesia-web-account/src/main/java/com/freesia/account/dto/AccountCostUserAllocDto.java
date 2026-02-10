package com.freesia.account.dto;

import com.freesia.dto.BaseDto;
import com.freesia.dto.SysUserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 费用分摊表 数据传输对象
 * @date 2025-10-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "费用分摊表 数据传输对象")
public class AccountCostUserAllocDto extends BaseDto {
    @Schema(description = "记账ID")
    private Long costId;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "分摊金额")
    private BigDecimal amount;
    @Schema(description = "分摊时间")
    private Date operateTime;
    @Schema(description = "是否分摊（0-否，1-是）")
    private Boolean allocFlag;
    @Schema(description = "分摊用户")
    private SysUserDto sysUserDto;
}
