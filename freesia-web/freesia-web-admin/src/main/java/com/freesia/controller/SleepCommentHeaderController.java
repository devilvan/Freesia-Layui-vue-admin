package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.vo.SleepCommentHeaderVo;
import com.freesia.dto.SleepCommentHeaderDto;
import com.freesia.service.SleepCommentHeaderService;
import com.freesia.converter.SleepCommentHeaderConverter;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 睡眠产品评论 控制器
 * @date 2026-03-23
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sleepCommentHeaderController")
@Tag(name = "SleepCommentHeaderController", description = "睡眠产品评论 控制器")
public class SleepCommentHeaderController extends BaseController {
    private final SleepCommentHeaderService sleepCommentHeaderService;
    private final SleepCommentHeaderConverter sleepCommentHeaderConverter;

    /**
     * 保存睡眠产品评论信息
     *
     * @param sleepCommentHeaderVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存睡眠产品评论信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SleepCommentHeaderVo sleepCommentHeaderVo) {
        SleepCommentHeaderDto sleepCommentHeaderDto = sleepCommentHeaderConverter.convertVo2Dto(sleepCommentHeaderVo);
        sleepCommentHeaderService.saveUpdate(sleepCommentHeaderDto);
        return R.ok();
    }

    /**
     * 批量保存睡眠产品评论信息
     *
     * @param sleepCommentHeaderVoList 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存睡眠产品评论信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SleepCommentHeaderVo> sleepCommentHeaderVoList) {
        List<SleepCommentHeaderDto> sleepCommentHeaderDtoList = sleepCommentHeaderConverter.convertBatchVo2Dto(sleepCommentHeaderVoList);
        sleepCommentHeaderService.saveUpdateBatch(sleepCommentHeaderDtoList);
        return R.ok();
    }

    /**
     * 查询睡眠产品评论分页信息
     *
     * @param sleepCommentHeaderVo 查询条件
     * @param pageQuery            分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询睡眠产品评论分页信息")
    @GetMapping(value = "findPageSleepCommentHeader")
    public TableResult<SleepCommentHeaderDto> findPageSleepCommentHeader(SleepCommentHeaderVo sleepCommentHeaderVo, PageQuery pageQuery) {
        SleepCommentHeaderDto sleepCommentHeaderDto = sleepCommentHeaderConverter.convertVo2Dto(sleepCommentHeaderVo);
        return sleepCommentHeaderService.findPage(sleepCommentHeaderDto, pageQuery);
    }

    /**
     * 条件查询睡眠产品评论
     *
     * @param sleepCommentHeaderVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询睡眠产品评论")
    @GetMapping(value = "findSleepCommentHeader")
    public R<SleepCommentHeaderDto> findSleepCommentHeader(SleepCommentHeaderVo sleepCommentHeaderVo) {
        SleepCommentHeaderDto sleepCommentHeaderDto = sleepCommentHeaderConverter.convertVo2Dto(sleepCommentHeaderVo);
        sleepCommentHeaderDto = sleepCommentHeaderService.findOne(sleepCommentHeaderDto);
        return R.ok(sleepCommentHeaderDto);
    }

    /**
     * 条件查询睡眠产品评论
     *
     * @param sleepCommentHeaderVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询睡眠产品评论")
    @GetMapping(value = "findListSleepCommentHeader")
    public R<List<SleepCommentHeaderDto>> findListSleepCommentHeader(SleepCommentHeaderVo sleepCommentHeaderVo) {
        SleepCommentHeaderDto sleepCommentHeaderDto = sleepCommentHeaderConverter.convertVo2Dto(sleepCommentHeaderVo);
        List<SleepCommentHeaderDto> sleepCommentHeaderDtoList = sleepCommentHeaderService.findList(sleepCommentHeaderDto);
        return R.ok(sleepCommentHeaderDtoList);
    }

    /**
     * 删除睡眠产品评论
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除睡眠产品评论")
    @PostMapping(value = "deleteSleepCommentHeader")
    public R<Void> deleteSleepCommentHeader(@RequestBody List<Long> idList) {
        sleepCommentHeaderService.deleteBatch(idList);
        return R.ok();
    }

    @SaIgnore
    @PostMapping(value = "handleTrustPilot")
    public R<Void> handleTrustPilot(@RequestBody SleepCommentHeaderVo sleepCommentHeaderVo) {
        sleepCommentHeaderService.handleTrustPilot(sleepCommentHeaderVo);
        return R.ok();
    }

    @SaIgnore
    @PostMapping(value = "exportTrustPilot")
    public R<Void> exportTrustPilot(@RequestBody SleepCommentHeaderVo sleepCommentHeaderVo) {
        sleepCommentHeaderService.exportTrustPilot(sleepCommentHeaderVo);
        return R.ok();
    }

    @SaIgnore
    @PostMapping(value = "handleReddit")
    public R<Void> handleReddit(@RequestBody SleepCommentHeaderVo sleepCommentHeaderVo) {
        sleepCommentHeaderService.handleReddit(sleepCommentHeaderVo);
        return R.ok();
    }

    @SaIgnore
    @PostMapping(value = "handle3B")
    public R<Void> handle3B(@RequestBody SleepCommentHeaderVo sleepCommentHeaderVo) {
        sleepCommentHeaderService.handle3B(sleepCommentHeaderVo);
        return R.ok();
    }

}
