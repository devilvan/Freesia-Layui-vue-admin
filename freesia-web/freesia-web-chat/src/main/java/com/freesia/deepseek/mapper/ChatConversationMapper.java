package com.freesia.deepseek.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.deepseek.po.ChatConversationPo;
import com.freesia.deepseek.dto.ChatConversationDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 交互式会话 持久层
 * @date 2026-07-19
 */
@Mapper
public interface ChatConversationMapper extends BaseMapper<ChatConversationPo> {
    /**
     * 分页查询交互式会话信息
     *
     * @param chatConversationDto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<ChatConversationPo> findPage(@Param(value = "dto") ChatConversationDto chatConversationDto, @Param("page") Page<ChatConversationPo> page);

    /**
     * 查询交互式会话信息
     *
     * @param chatConversationDto 查询条件
     * @return 分页信息
     */
    List<ChatConversationPo> findList(@Param(value = "dto") ChatConversationDto chatConversationDto);

    /**
     * 查询交互式会话信息
     *
     * @param chatConversationDto 查询条件
     * @return 分页信息
     */
    ChatConversationPo findOne(@Param(value = "dto") ChatConversationDto chatConversationDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<ChatConversationPo> list);
}
