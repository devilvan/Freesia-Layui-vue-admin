package com.freesia.notice.controller;

import com.freesia.controller.BaseController;
import com.freesia.notice.dto.SysNoticeDto;
import com.freesia.notice.service.SysNoticeService;
import com.freesia.notice.vo.SysNoticeVo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 消息公告表 控制器
 * @date 2025-06-06
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sysNoticeController")
@Tag(name = "SysNoticeController", description = "消息公告表 控制器")
public class SysNoticeController extends BaseController {
    private final SysNoticeService sysNoticeService;

    /**
     * 保存消息公告表信息
     *
     * @param sysNoticeVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存消息公告表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysNoticeVo sysNoticeVo) {
        SysNoticeDto sysNoticeDto = UCopy.copyVo2Dto(sysNoticeVo, SysNoticeDto.class);
        Date[] effectiveTime = sysNoticeVo.getEffectiveTime();
        if (UEmpty.isNotEmpty(effectiveTime) && effectiveTime.length == 2) {
            sysNoticeDto.setEffectiveTimeFrom(effectiveTime[0]);
            sysNoticeDto.setEffectiveTimeTo(effectiveTime[1]);
        }
        sysNoticeService.saveUpdate(sysNoticeDto);
        return R.ok();
    }

    /**
     * 批量保存消息公告表信息
     *
     * @param sysNoticeVoList 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存消息公告表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<SysNoticeVo> sysNoticeVoList) {
        List<SysNoticeDto> sysNoticeDtoList = UCopy.fullCopyList(sysNoticeVoList, SysNoticeDto.class);
        sysNoticeService.saveUpdateBatch(sysNoticeDtoList);
        return R.ok();
    }

    /**
     * 查询消息公告表分页信息
     *
     * @param sysNoticeVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询消息公告表分页信息")
    @GetMapping(value = "findPageSysNotice")
    public TableResult<SysNoticeDto> findPageSysNotice(SysNoticeVo sysNoticeVo, PageQuery pageQuery) {
        SysNoticeDto sysNoticeDto = UCopy.copyVo2Dto(sysNoticeVo, SysNoticeDto.class);
        Date[] effectiveTime = sysNoticeVo.getEffectiveTime();
        if (UEmpty.isNotEmpty(effectiveTime) && effectiveTime.length == 2) {
            sysNoticeDto.setEffectiveTimeFrom(effectiveTime[0]);
            sysNoticeDto.setEffectiveTimeTo(effectiveTime[1]);
        }
        return sysNoticeService.findPageSysNotice(sysNoticeDto, pageQuery);
    }

    /**
     * 条件查询消息公告表
     *
     * @param sysNoticeVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询消息公告表")
    @GetMapping(value = "findSysNotice")
    public R<SysNoticeDto> findSysNotice(SysNoticeVo sysNoticeVo) {
        SysNoticeDto sysNoticeDto = UCopy.copyVo2Dto(sysNoticeVo, SysNoticeDto.class);
        SysNoticeDto tableResult = sysNoticeService.findSysNotice(sysNoticeDto);
        return R.ok(tableResult);
    }

    /**
     * 删除消息公告表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除消息公告表")
    @PostMapping(value = "deleteSysNotice")
    public R<Void> deleteSysNotice(@RequestBody List<Long> idList) {
        sysNoticeService.deleteSysNotice(idList);
        return R.ok();
    }
}
