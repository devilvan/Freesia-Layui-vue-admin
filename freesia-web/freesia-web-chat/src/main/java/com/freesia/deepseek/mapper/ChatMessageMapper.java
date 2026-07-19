package com.freesia.deepseek.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.deepseek.po.ChatMessagePo;
import com.freesia.deepseek.dto.ChatMessageDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 交互式会话-消息 持久层
 * @date 2026-07-19
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessagePo> {
    /**
     * 分页查询交互式会话-消息信息
     *
     * @param chatMessageDto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<ChatMessagePo> findPage(@Param(value = "dto") ChatMessageDto chatMessageDto, @Param("page") Page<ChatMessagePo> page);

    /**
     * 查询交互式会话-消息信息
     *
     * @param chatMessageDto 查询条件
     * @return 分页信息
     */
    List<ChatMessagePo> findList(@Param(value = "dto") ChatMessageDto chatMessageDto);

    /**
     * 查询交互式会话-消息信息
     *
     * @param chatMessageDto 查询条件
     * @return 分页信息
     */
    ChatMessagePo findOne(@Param(value = "dto") ChatMessageDto chatMessageDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<ChatMessagePo> list);
}
