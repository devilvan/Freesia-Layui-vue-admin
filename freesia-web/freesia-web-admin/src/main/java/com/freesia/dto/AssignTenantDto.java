package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 分配租户 数据传输对象
 * @date 2024-02-03
 */
@Data
@Schema(description = "分配租户 数据传输对象")
public class AssignTenantDto {
    @Schema(description = "租户ID")
    private Long tenantId;
    @Schema(description = "用户ID")
    private List<Long> userIdList;
}
