package com.freesia.deepseek.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.deepseek.vo.ChatConversationVo;
import com.freesia.deepseek.dto.ChatConversationDto;
import com.freesia.deepseek.po.ChatConversationPo;
import com.freesia.deepseek.service.ChatConversationService;
import com.freesia.deepseek.converter.ChatConversationConverter;
import com.freesia.deepseek.mapper.ChatConversationMapper;
import com.freesia.deepseek.repository.ChatConversationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 交互式会话 业务逻辑类
 * @date 2026-07-19
 */
@Service
@RequiredArgsConstructor
public class ChatConversationServiceImpl extends BaseServiceImpl<ChatConversationMapper, ChatConversationVo, ChatConversationDto, ChatConversationPo> implements ChatConversationService {
    private final ChatConversationRepository chatConversationRepository;
    private final ChatConversationMapper chatConversationMapper;
    private final ChatConversationConverter chatConversationConverter;

    @Override
    protected MapStructConverter<ChatConversationVo, ChatConversationDto, ChatConversationPo> getMapStructConverter() {
        return chatConversationConverter;
    }

    @Override
    protected JpaRepository<ChatConversationPo, Long> getRepository() {
    return chatConversationRepository;
    }

    @Override
    protected Class<ChatConversationDto> getDtoClass() {
        return ChatConversationDto.class;
    }

    @Override
    protected Class<ChatConversationPo> getPoClass() {
        return ChatConversationPo.class;
    }

    @Override
    protected Wrapper<ChatConversationPo> buildQueryWrapper(@NonNull ChatConversationDto chatConversationDto) {
        return new LambdaQueryWrapper<ChatConversationPo>()
                .eq(ChatConversationPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(chatConversationDto.getId()), ChatConversationPo::getId, chatConversationDto.getId())
                .eq(UEmpty.isNotEmpty(chatConversationDto.getProviderCode()), ChatConversationPo::getProviderCode, chatConversationDto.getProviderCode())
                .eq(UEmpty.isNotEmpty(chatConversationDto.getUserId()), ChatConversationPo::getUserId, chatConversationDto.getUserId())
                .eq(UEmpty.isNotEmpty(chatConversationDto.getTitle()), ChatConversationPo::getTitle, chatConversationDto.getTitle())
                .eq(UEmpty.isNotEmpty(chatConversationDto.getChatMode()), ChatConversationPo::getChatMode, chatConversationDto.getChatMode())
                ;
    }

    @Override
    public TableResult<ChatConversationDto> findPage(ChatConversationDto dto, PageQuery pageQuery) {
        Page<ChatConversationPo> page = chatConversationMapper.findPage(dto, pageQuery.build());
        return TableResult.build(chatConversationConverter.convertPagePo2Dto(page));
    }

    @Override
    public List<ChatConversationDto> findList(ChatConversationDto dto) {
        return chatConversationConverter.convertBatchPo2Dto(chatConversationMapper.findList(dto));
        return chatConversationMapper.findList(dto);
    }

    @Override
    public ChatConversationDto findOne(ChatConversationDto dto) {
        return chatConversationConverter.convertPo2Dto(chatConversationMapper.findOne(dto));
    }
}
