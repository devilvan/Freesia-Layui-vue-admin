package com.freesia.notice.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 标记已读 值对象
 * @date 2025-09-20
 */
@Data
public class MarkReadDto {
    /**
     * 主键集合
     */
    private List<Long> idList;
    /**
     * 消息类型
     */
    private String type;
    /**
     * 用户ID
     */
    private Long userId;
}
