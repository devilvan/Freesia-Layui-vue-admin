package com.freesia.deepseek.controller;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.deepseek.vo.ChatMessageVo;
import com.freesia.deepseek.dto.ChatMessageDto;
import com.freesia.deepseek.service.ChatMessageService;
import com.freesia.deepseek.converter.ChatMessageConverter;
import com.freesia.controller.BaseController;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 交互式会话-消息 控制器
 * @date 2026-07-19
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/chatMessageController")
@Tag(name = "ChatMessageController", description = "交互式会话-消息 控制器")
public class ChatMessageController extends BaseController {
    private final ChatMessageService chatMessageService;
    private final ChatMessageConverter chatMessageConverter;

    /**
     * 保存交互式会话-消息信息
     *
     * @param chatMessageVo    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存交互式会话-消息信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody ChatMessageVo chatMessageVo) {
        ChatMessageDto chatMessageDto = chatMessageConverter.convertVo2Dto(chatMessageVo);
        chatMessageService.saveUpdate(chatMessageDto);
        return R.ok();
    }

    /**
     * 批量保存交互式会话-消息信息
     *
     * @param chatMessageVoList    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存交互式会话-消息信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<ChatMessageVo> chatMessageVoList) {
        List<ChatMessageDto> chatMessageDtoList = chatMessageConverter.convertBatchVo2Dto(chatMessageVoList);
        chatMessageService.saveUpdateBatch(chatMessageDtoList);
        return R.ok();
    }

    /**
     * 查询交互式会话-消息分页信息
     *
     * @param chatMessageVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询交互式会话-消息分页信息")
    @GetMapping(value = "findPageChatMessage")
    public TableResult<ChatMessageDto> findPageChatMessage(ChatMessageVo chatMessageVo, PageQuery pageQuery) {
        ChatMessageDto chatMessageDto = chatMessageConverter.convertVo2Dto(chatMessageVo);
        return chatMessageService.findPage(chatMessageDto, pageQuery);
    }

    /**
     * 条件查询交互式会话-消息
     *
     * @param chatMessageVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询交互式会话-消息")
    @GetMapping(value = "findChatMessage")
    public R<ChatMessageDto> findChatMessage(ChatMessageVo chatMessageVo) {
        ChatMessageDto chatMessageDto = chatMessageConverter.convertVo2Dto(chatMessageVo);
        chatMessageDto = chatMessageService.findOne(chatMessageDto);
        return R.ok(chatMessageDto);
    }

    /**
    * 条件查询交互式会话-消息
    *
    * @param chatMessageVo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询交互式会话-消息")
    @GetMapping(value = "findListChatMessage")
    public R<List<ChatMessageDto>> findListChatMessage(ChatMessageVo chatMessageVo) {
        ChatMessageDto chatMessageDto = chatMessageConverter.convertVo2Dto(chatMessageVo);
        List<ChatMessageDto> chatMessageDtoList = chatMessageService.findList(chatMessageDto);
        return R.ok(chatMessageDtoList);
    }

    /**
     * 删除交互式会话-消息
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除交互式会话-消息")
    @PostMapping(value = "deleteChatMessage")
    public R<Void> deleteChatMessage(@RequestBody List<Long> idList) {
        chatMessageService.deleteBatch(idList);
        return R.ok();
    }
}
