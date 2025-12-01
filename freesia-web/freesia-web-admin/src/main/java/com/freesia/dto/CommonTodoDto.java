package com.freesia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 待办事项表 数据传输对象
 * @date 2025-11-27
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
    @Schema(description = "描述（副标题）")
    private String desc;
    @Schema(description = "内容")
    private String content;
    @Schema(description = "状态（UNFINISHED-未完成；FINISHED-已完成）")
    private String status;
    @Schema(description = "提醒时间")
    private Date dueTime;
    @Schema(description = "发送提醒标识（0-否；1-是）")
    private Boolean reminderSendFlag;
    @Schema(description = "优先级（0-高；1-中；2-低）")
    private Integer priority;
    @Schema(description = "备注")
    private String remark;
}
