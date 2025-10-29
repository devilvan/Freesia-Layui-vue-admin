package com.freesia.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 标记已读 值对象
 * @date 2025-09-20
 */
@Data
public class MarkReadDto {
    @Schema(description = "主键集合")
    private List<Long> idList;
    @Schema(description = "消息类型")
    private String type;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "创建时间从")
    private Date createTimeFrom;
    @Schema(description = "创建时间到")
    private Date createTimeTo;
}
