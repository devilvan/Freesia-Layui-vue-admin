package com.freesia.deepseek.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.deepseek.dto.ChatConversationDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 交互式会话 业务逻辑接口
 * @date 2026-07-19
 */
public interface ChatConversationService {
    /**
     * 保存交互式会话信息
     *
     * @param chatConversationDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    ChatConversationDto saveUpdate(ChatConversationDto chatConversationDto);

    /**
     * 批量保存交互式会话信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<ChatConversationDto> saveUpdateBatch(List<ChatConversationDto> list);

    /**
     * 查询交互式会话信息
     *
     * @param chatConversationDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<ChatConversationDto> findPage(ChatConversationDto chatConversationDto, PageQuery pageQuery);

    /**
     * 条件查询交互式会话信息
     *
     * @param chatConversationDto 查询条件
     * @return 交互式会话信息
     */
    ChatConversationDto findOne(ChatConversationDto chatConversationDto);

    /**
     * 条件查询交互式会话信息
     *
     * @param chatConversationDto 查询条件
     * @return 交互式会话信息
     */
    List<ChatConversationDto> findList(ChatConversationDto chatConversationDto);

    /**
     * 删除交互式会话信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);
}
