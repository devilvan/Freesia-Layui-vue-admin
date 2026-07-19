package com.freesia.deepseek.controller;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.deepseek.vo.ChatConversationVo;
import com.freesia.deepseek.dto.ChatConversationDto;
import com.freesia.deepseek.service.ChatConversationService;
import com.freesia.deepseek.converter.ChatConversationConverter;
import com.freesia.controller.BaseController;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 交互式会话 控制器
 * @date 2026-07-19
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/chatConversationController")
@Tag(name = "ChatConversationController", description = "交互式会话 控制器")
public class ChatConversationController extends BaseController {
    private final ChatConversationService chatConversationService;
    private final ChatConversationConverter chatConversationConverter;

    /**
     * 保存交互式会话信息
     *
     * @param chatConversationVo    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存交互式会话信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody ChatConversationVo chatConversationVo) {
        ChatConversationDto chatConversationDto = chatConversationConverter.convertVo2Dto(chatConversationVo);
        chatConversationService.saveUpdate(chatConversationDto);
        return R.ok();
    }

    /**
     * 批量保存交互式会话信息
     *
     * @param chatConversationVoList    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存交互式会话信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<ChatConversationVo> chatConversationVoList) {
        List<ChatConversationDto> chatConversationDtoList = chatConversationConverter.convertBatchVo2Dto(chatConversationVoList);
        chatConversationService.saveUpdateBatch(chatConversationDtoList);
        return R.ok();
    }

    /**
     * 查询交互式会话分页信息
     *
     * @param chatConversationVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询交互式会话分页信息")
    @GetMapping(value = "findPageChatConversation")
    public TableResult<ChatConversationDto> findPageChatConversation(ChatConversationVo chatConversationVo, PageQuery pageQuery) {
        ChatConversationDto chatConversationDto = chatConversationConverter.convertVo2Dto(chatConversationVo);
        return chatConversationService.findPage(chatConversationDto, pageQuery);
    }

    /**
     * 条件查询交互式会话
     *
     * @param chatConversationVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询交互式会话")
    @GetMapping(value = "findChatConversation")
    public R<ChatConversationDto> findChatConversation(ChatConversationVo chatConversationVo) {
        ChatConversationDto chatConversationDto = chatConversationConverter.convertVo2Dto(chatConversationVo);
        chatConversationDto = chatConversationService.findOne(chatConversationDto);
        return R.ok(chatConversationDto);
    }

    /**
    * 条件查询交互式会话
    *
    * @param chatConversationVo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询交互式会话")
    @GetMapping(value = "findListChatConversation")
    public R<List<ChatConversationDto>> findListChatConversation(ChatConversationVo chatConversationVo) {
        ChatConversationDto chatConversationDto = chatConversationConverter.convertVo2Dto(chatConversationVo);
        List<ChatConversationDto> chatConversationDtoList = chatConversationService.findList(chatConversationDto);
        return R.ok(chatConversationDtoList);
    }

    /**
     * 删除交互式会话
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除交互式会话")
    @PostMapping(value = "deleteChatConversation")
    public R<Void> deleteChatConversation(@RequestBody List<Long> idList) {
        chatConversationService.deleteBatch(idList);
        return R.ok();
    }
}
