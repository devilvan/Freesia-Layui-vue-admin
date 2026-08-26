package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Evad.Wu
 * @Description 全部已读 请求参数
 * @date 2026-08-25
 */
@Data
@Schema(description = "全部已读 请求参数")
public class MarkAllReadDto {
    @Schema(description = "通知类型")
    private String type;
}
