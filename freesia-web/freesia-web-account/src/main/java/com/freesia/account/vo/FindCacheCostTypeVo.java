package com.freesia.account.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Evad.Wu
 * @Description 自动完成-根据输入查询图标类型和URL 值对象
 * @date 2025-09-09
 */
@Data
public class FindCacheCostTypeVo {
    @Schema(description = "开销描述")
    private String costDesc;
}
