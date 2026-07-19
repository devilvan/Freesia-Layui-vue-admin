package com.freesia.deepseek.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.deepseek.dto.ChatMessageDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 交互式会话-消息 业务逻辑接口
 * @date 2026-07-19
 */
public interface ChatMessageService {
    /**
     * 保存交互式会话-消息信息
     *
     * @param chatMessageDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    ChatMessageDto saveUpdate(ChatMessageDto chatMessageDto);

    /**
     * 批量保存交互式会话-消息信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<ChatMessageDto> saveUpdateBatch(List<ChatMessageDto> list);

    /**
     * 查询交互式会话-消息信息
     *
     * @param chatMessageDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<ChatMessageDto> findPage(ChatMessageDto chatMessageDto, PageQuery pageQuery);

    /**
     * 条件查询交互式会话-消息信息
     *
     * @param chatMessageDto 查询条件
     * @return 交互式会话-消息信息
     */
    ChatMessageDto findOne(ChatMessageDto chatMessageDto);

    /**
     * 条件查询交互式会话-消息信息
     *
     * @param chatMessageDto 查询条件
     * @return 交互式会话-消息信息
     */
    List<ChatMessageDto> findList(ChatMessageDto chatMessageDto);

    /**
     * 删除交互式会话-消息信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);
}
