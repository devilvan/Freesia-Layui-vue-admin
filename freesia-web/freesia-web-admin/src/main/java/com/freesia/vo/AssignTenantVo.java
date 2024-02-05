package com.freesia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 分配租户 值对象
 * @date 2024-02-03
 */
@Data
@Schema(description = "分配租户 值对象")
public class AssignTenantVo {
    @Schema(description = "租户ID")
    @NotEmpty(message = "租户ID不能为空")
    private String tenantId;
    @Schema(description = "用户ID")
    private List<String> userIdList;
}
