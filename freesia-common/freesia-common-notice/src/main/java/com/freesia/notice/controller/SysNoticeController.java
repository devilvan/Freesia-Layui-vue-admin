package com.freesia.notice.controller;

import com.freesia.controller.BaseController;
import com.freesia.exception.ServiceException;
import com.freesia.notice.constant.NoticeModule;
import com.freesia.notice.constant.SysNoticeType;
import com.freesia.notice.dto.MarkReadDto;
import com.freesia.notice.dto.SysNoticeDto;
import com.freesia.notice.entity.FindPageSysNoticeEntity;
import com.freesia.notice.entity.FindPublishedAnnouncementEntity;
import com.freesia.notice.exception.NoticeException;
import com.freesia.notice.service.SysNoticeService;
import com.freesia.notice.vo.SysNoticeVo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.satoken.util.USecurity;
import com.freesia.sse.component.SseEmitterManager;
import com.freesia.sse.constant.SseTopic;
import com.freesia.sse.dto.SseMessageDto;
import com.freesia.util.UCalendar;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private final SseEmitterManager sseEmitterManager;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    /**
     * 保存消息公告表信息
     *
     * @param sysNoticeVo 待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存消息公告表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody SysNoticeVo sysNoticeVo) {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new ServiceException(NoticeModule.NOTICE_MANAGEMENT, "user.not.exists", new Object[]{}));
        String type = sysNoticeVo.getType();
        sysNoticeVo.setPublisherId(userId);
        SysNoticeType sysNoticeType = SysNoticeType.getInstanceByCode(type);
        if (UEmpty.isNull(sysNoticeType)) {
            throw new NoticeException("notice.type.invalid", new Object[]{sysNoticeType});
        }
        SysNoticeDto sysNoticeDto;
        if (sysNoticeType.equals(SysNoticeType.ANNOUNCEMENT)) {
            Date effectiveTimeFrom = sysNoticeVo.getEffectiveTimeFrom();
            Date effectiveTimeTo = sysNoticeVo.getEffectiveTimeTo();
            // 生效时间从 -> 生效时间到
            Calendar calendar = UCalendar.getInstance();
            if (effectiveTimeFrom != null && effectiveTimeTo != null) {
                calendar.setTime(effectiveTimeFrom);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                sysNoticeVo.setEffectiveTimeFrom(calendar.getTime());
                calendar.setTime(effectiveTimeTo);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                sysNoticeVo.setEffectiveTimeTo(calendar.getTime());
            } else if (effectiveTimeFrom != null) {
                // 生效时间从 -> 长期
                calendar.setTime(effectiveTimeFrom);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                sysNoticeVo.setEffectiveTimeFrom(calendar.getTime());
            }
            sysNoticeDto = UCopy.copyVo2Dto(sysNoticeVo, SysNoticeDto.class);
            scheduledThreadPoolExecutor.schedule(() -> {
                SseMessageDto sseMessageDto = new SseMessageDto();
                sseMessageDto.setTopicList(Collections.singletonList(SseTopic.GLOBAL_SSE.getKey()));
                sseMessageDto.setContent(sysNoticeDto.getContent());
                sseEmitterManager.publishAll(sseMessageDto);
            }, 5, TimeUnit.SECONDS);
        } else {
            sysNoticeDto = UCopy.copyVo2Dto(sysNoticeVo, SysNoticeDto.class);
        }
        if (UEmpty.isNotNull(sysNoticeDto)) {
            sysNoticeService.saveUpdate(sysNoticeDto);
        }
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
    public TableResult<FindPageSysNoticeEntity> findPageSysNotice(SysNoticeVo sysNoticeVo, PageQuery pageQuery) {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new ServiceException(NoticeModule.NOTICE_MANAGEMENT, "user.not.exists", new Object[]{}));
        sysNoticeVo.setUserId(userId);
        SysNoticeDto sysNoticeDto = UCopy.copyVo2Dto(sysNoticeVo, SysNoticeDto.class);
        Date[] effectiveTime = sysNoticeVo.getEffectiveTime();
        if (UEmpty.isNotEmpty(effectiveTime) && effectiveTime.length == 2) {
            sysNoticeDto.setEffectiveTimeFrom(effectiveTime[0]);
            sysNoticeDto.setEffectiveTimeTo(effectiveTime[1]);
        }
        return sysNoticeService.findPageSysNotice(sysNoticeDto, pageQuery);
    }

    /**
     * 查询消息公告表集合
     *
     * @param sysNoticeVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "查询消息公告表集合")
    @GetMapping(value = "findListSysNotice")
    public R<List<FindPageSysNoticeEntity>> findListSysNotice(SysNoticeVo sysNoticeVo) {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new ServiceException(NoticeModule.NOTICE_MANAGEMENT, "user.not.exists", new Object[]{}));
        sysNoticeVo.setUserId(userId);
        SysNoticeDto sysNoticeDto = UCopy.copyVo2Dto(sysNoticeVo, SysNoticeDto.class);
        Date[] effectiveTime = sysNoticeVo.getEffectiveTime();
        if (UEmpty.isNotEmpty(effectiveTime) && effectiveTime.length == 2) {
            sysNoticeDto.setEffectiveTimeFrom(effectiveTime[0]);
            sysNoticeDto.setEffectiveTimeTo(effectiveTime[1]);
        }
        List<FindPageSysNoticeEntity> findPageSysNoticeEntityList = sysNoticeService.findListSysNotice(sysNoticeDto);
        return R.ok(findPageSysNoticeEntityList);
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
     * 查询已发布的公告
     *
     * @return 形式返回
     */
    @Operation(summary = "查询已发布的公告")
    @GetMapping(value = "findPublishedAnnouncement")
    public R<List<FindPublishedAnnouncementEntity>> findPublishedAnnouncement() {
        List<FindPublishedAnnouncementEntity> sysNoticeDtoList = sysNoticeService.findPublishedAnnouncement();
        return R.ok(sysNoticeDtoList);
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

    /**
     * 查询未读消息/公告数量
     *
     * @param sysNoticeVo 主键
     * @return 形式返回
     */
    @Operation(summary = "查询未读消息/公告数量")
    @GetMapping(value = "findUnreadCount")
    public R<Integer> findUnreadCount(SysNoticeVo sysNoticeVo) {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new ServiceException(NoticeModule.NOTICE_MANAGEMENT, "user.not.exists", new Object[]{}));
        sysNoticeVo.setUserId(userId);
        Integer count = sysNoticeService.findUnreadCount(sysNoticeVo);
        return R.ok(count);
    }

    /**
     * 标记已读
     *
     * @param markReadDto 入参
     * @return 形式返回
     */
    @Operation(summary = "标记已读")
    @PostMapping(value = "markRead")
    public R<Integer> markRead(@RequestBody MarkReadDto markReadDto) {
        Long userId = Optional.ofNullable(USecurity.getUserId()).orElseThrow(() -> new ServiceException(NoticeModule.NOTICE_MANAGEMENT, "user.not.exists", new Object[]{}));
        markReadDto.setUserId(userId);
        Integer count = sysNoticeService.markRead(markReadDto);
        return R.ok(count);
    }
}
