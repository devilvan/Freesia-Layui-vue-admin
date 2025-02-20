package com.freesia.sse.dto;

import com.freesia.dto.BaseSseMessageDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Evad.Wu
 * @Description SSE (Server-Sent Events) 消息订阅 数据传输类
 * @date 2024-10-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SseMessageDto extends BaseSseMessageDto {
}
