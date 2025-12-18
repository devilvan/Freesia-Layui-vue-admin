package com.freesia.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 待办事项表 数据传输对象
 * @date 2025-12-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "待办事项表 数据传输对象")
public class CommonTodoDto extends BaseDto {
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "内容")
    private String content;
    @Schema(description = "状态（UNFINISHED-未完成；FINISHED-已完成）")
    private String status;
    @Schema(description = "提醒时间")
    private Date dueTime;
    @Schema(description = "发送提醒标识（0-否；1-是）")
    private Integer reminderSendFlag;
    @Schema(description = "优先级（0-高；1-中；2-低）")
    private Integer priority;
}
