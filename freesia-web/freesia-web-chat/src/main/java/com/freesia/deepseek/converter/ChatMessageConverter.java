package com.freesia.deepseek.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.deepseek.dto.ChatMessageDto;
import com.freesia.deepseek.po.ChatMessagePo;
import com.freesia.deepseek.vo.ChatMessageVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 交互式会话-消息 MapStruct转换器
 * @date 2026-07-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMessageConverter extends MapStructConverter<ChatMessageVo, ChatMessageDto, ChatMessagePo> {
}
