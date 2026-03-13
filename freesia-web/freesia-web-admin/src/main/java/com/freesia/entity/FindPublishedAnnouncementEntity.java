package com.freesia.entity;

import com.freesia.dto.SysNoticeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Bliss.Wu
 * @Description 查询已发布的公告 实体类
 * @date 2025-07-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindPublishedAnnouncementEntity extends SysNoticeDto {
    /**
     * 发布人名称
     */
    private String publisherName;
    /**
     * 通知类型名称
     */
    private String typeName;
}
