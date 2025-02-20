package com.freesia.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description SSE (Server-Sent Events) 消息订阅 数据传输-父类
 * @date 2024-10-22
 */
@Data
public class BaseSseMessageDto {
    /**
     * 主题
     */
    @NotEmpty(message = "{not.null}")
    private List<String> topicList;
    /**
     * 订阅的用户ID
     */
    private List<Long> userIdList;
    /**
     * 发布的消息
     */
    @NotEmpty(message = "{not.null}")
    private String content;
}
