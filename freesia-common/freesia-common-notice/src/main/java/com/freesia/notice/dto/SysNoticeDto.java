package com.freesia.notice.dto;

import com.freesia.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.math.BigDecimal;

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
}
