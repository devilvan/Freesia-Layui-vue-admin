package com.freesia.account.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 新增费用分摊-根据分摊用户ID查询用户信息 数据传输对象
 * @date 2025-10-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindListSysUserByIdDto extends AccountCostUserAllocDto {
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "用户昵称")
    private String nickName;
}
