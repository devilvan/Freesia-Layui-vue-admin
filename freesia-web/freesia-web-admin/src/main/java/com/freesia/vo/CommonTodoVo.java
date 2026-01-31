package com.freesia.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 待办事项表 值对象
 * @date 2026-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "待办事项表 值对象")
public class CommonTodoVo extends BaseVo {
    @Schema(description = "用户ID")
    @JsonAlias(value = {"userId"})
    private Long userId;
    @Schema(description = "标题")
    @JsonAlias(value = {"title"})
    private String title;
    @Schema(description = "内容")
    @JsonAlias(value = {"content"})
    private String content;
    @Schema(description = "状态（0-未完成；1-已完成）")
    @JsonAlias(value = {"status"})
    private Boolean status;
    @Schema(description = "提醒时间")
    @JsonAlias(value = {"dueTime"})
    private Date dueTime;
    @Schema(description = "发送提醒标识（0-否；1-是）")
    @JsonAlias(value = {"reminderSendFlag"})
    private Integer reminderSendFlag;
    @Schema(description = "优先级（0-高；1-中；2-低）")
    @JsonAlias(value = {"priority"})
    private Integer priority;
}
