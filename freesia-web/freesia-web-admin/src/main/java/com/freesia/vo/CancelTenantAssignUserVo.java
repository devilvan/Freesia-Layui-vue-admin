package com.freesia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 取消将租户分配给用户 值对象
 * @date 2024-02-05
 */
@Data
@Schema(description = "取消将租户分配给用户 值对象")
public class CancelTenantAssignUserVo {
    @Schema(description = "租户ID")
    @NotEmpty(message = "{not.null}")
    private String tenantId;
    @Schema(description = "待取消分配的用户ID")
    @NotEmpty(message = "{not.null}")
    private List<Long> userIdList;
}
