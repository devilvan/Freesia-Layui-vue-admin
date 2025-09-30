package com.freesia.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.constant.Constants;
import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 消息公告表 数据传输对象
 * @date 2025-06-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息公告表 数据传输对象")
public class SysNoticeDto extends BaseDto {
    @Schema(description = "标题")
    private String title;
    @Schema(description = "通知类型（SYS_NOTICE_TYPE）")
    private String type;
    @Schema(description = "生效时间从")
    private Date effectiveTimeFrom;
    @Schema(description = "生效时间到")
    private Date effectiveTimeTo;
    @Schema(description = "内容")
    private String content;
    @Schema(description = "发布人ID")
    private Long publisherId;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "已读标识（0-未读;1-已读）")
    private Boolean readFlag;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "消息所属类别")
    private String category;
    @Schema(description = "摘要")
    private String excerpt;
    @Schema(description = "创建时间从")
    private Date createTimeFrom;
    @Schema(description = "创建时间到")
    private Date createTimeTo;
    @Schema(description = "主键集合")
    private List<Long> idList;
}
