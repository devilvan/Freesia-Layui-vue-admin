package com.freesia.deepseek.converter;

import com.freesia.convert.MapStructConverter;
import com.freesia.deepseek.dto.ChatConversationDto;
import com.freesia.deepseek.po.ChatConversationPo;
import com.freesia.deepseek.vo.ChatConversationVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 交互式会话 MapStruct转换器
 * @date 2026-07-19
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatConversationConverter extends MapStructConverter<ChatConversationVo, ChatConversationDto, ChatConversationPo> {
}
