package com.freesia.notice.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 消息公告表 值对象
 * @date 2025-06-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息公告表 值对象")
public class SysNoticeVo extends BaseVo {
    @Schema(description = "标题")
    @JsonAlias(value = {"title"})
    private String title;
    @Schema(description = "通知类型（SYS_NOTICE_TYPE）")
    @JsonAlias(value = {"type"})
    private String type;
    @Schema(description = "生效时间")
    @JsonAlias(value = {"effectiveTime"})
    private Date[] effectiveTime;
    @Schema(description = "生效时间从")
    @JsonAlias(value = {"effectiveTimeFrom"})
    private Date effectiveTimeFrom;
    @Schema(description = "生效时间到")
    @JsonAlias(value = {"effectiveTimeTo"})
    private Date effectiveTimeTo;
    @Schema(description = "内容")
    @JsonAlias(value = {"content"})
    private String content;
    @Schema(description = "发布人ID")
    @JsonAlias(value = {"publisherId"})
    private Long publisherId;
    @Schema(description = "备注")
    @JsonAlias(value = {"remark"})
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
}
