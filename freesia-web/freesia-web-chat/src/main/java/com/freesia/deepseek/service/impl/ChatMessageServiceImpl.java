package com.freesia.deepseek.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.deepseek.vo.ChatMessageVo;
import com.freesia.deepseek.dto.ChatMessageDto;
import com.freesia.deepseek.po.ChatMessagePo;
import com.freesia.deepseek.service.ChatMessageService;
import com.freesia.deepseek.converter.ChatMessageConverter;
import com.freesia.deepseek.mapper.ChatMessageMapper;
import com.freesia.deepseek.repository.ChatMessageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.freesia.service.impl.BaseServiceImpl;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 交互式会话-消息 业务逻辑类
 * @date 2026-07-19
 */
@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl extends BaseServiceImpl<ChatMessageMapper, ChatMessageVo, ChatMessageDto, ChatMessagePo> implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatMessageConverter chatMessageConverter;

    @Override
    protected MapStructConverter<ChatMessageVo, ChatMessageDto, ChatMessagePo> getMapStructConverter() {
        return chatMessageConverter;
    }

    @Override
    protected JpaRepository<ChatMessagePo, Long> getRepository() {
    return chatMessageRepository;
    }

    @Override
    protected Class<ChatMessageDto> getDtoClass() {
        return ChatMessageDto.class;
    }

    @Override
    protected Class<ChatMessagePo> getPoClass() {
        return ChatMessagePo.class;
    }

    @Override
    protected Wrapper<ChatMessagePo> buildQueryWrapper(@NonNull ChatMessageDto chatMessageDto) {
        return new LambdaQueryWrapper<ChatMessagePo>()
                .eq(ChatMessagePo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(chatMessageDto.getId()), ChatMessagePo::getId, chatMessageDto.getId())
                .eq(UEmpty.isNotEmpty(chatMessageDto.getConversationId()), ChatMessagePo::getConversationId, chatMessageDto.getConversationId())
                .eq(UEmpty.isNotEmpty(chatMessageDto.getRole()), ChatMessagePo::getRole, chatMessageDto.getRole())
                .eq(UEmpty.isNotEmpty(chatMessageDto.getContent()), ChatMessagePo::getContent, chatMessageDto.getContent())
                .eq(UEmpty.isNotEmpty(chatMessageDto.getOrderNum()), ChatMessagePo::getOrderNum, chatMessageDto.getOrderNum())
                ;
    }

    @Override
    public TableResult<ChatMessageDto> findPage(ChatMessageDto dto, PageQuery pageQuery) {
        Page<ChatMessagePo> page = chatMessageMapper.findPage(dto, pageQuery.build());
        return TableResult.build(chatMessageConverter.convertPagePo2Dto(page));
    }

    @Override
    public List<ChatMessageDto> findList(ChatMessageDto dto) {
        return chatMessageConverter.convertBatchPo2Dto(chatMessageMapper.findList(dto));
    }

    @Override
    public ChatMessageDto findOne(ChatMessageDto dto) {
        return chatMessageConverter.convertPo2Dto(chatMessageMapper.findOne(dto));
    }
}
