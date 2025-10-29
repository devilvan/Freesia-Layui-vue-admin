package com.freesia.notice.entity;

import com.freesia.notice.dto.SysNoticeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description 查询消息公告表分页信息 实体类
 * @date 2025-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPageSysNoticeEntity extends SysNoticeDto {
    /**
     * 发布人名称
     */
    private String publisherName;
}
