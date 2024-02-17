package com.freesia.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 给角色分配用户 值对象
 * @date 2023-12-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "给角色分配用户 值对象")
public class AssignUserVo extends BaseVo {
    @Schema(description = "角色ID")
    @NotEmpty(message = "{not.null}")
    private String roleId;
    @Schema(description = "待分配的用户ID")
    @NotEmpty(message = "{not.null}")
    private List<Long> userIdList;
}
