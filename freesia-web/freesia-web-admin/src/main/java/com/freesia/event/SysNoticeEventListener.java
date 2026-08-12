package com.freesia.event;

import com.freesia.dto.SysNoticeDto;
import com.freesia.service.SysNoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author Evad.Wu
 * @Description 敏感信息日志 事件监听类
 * @date 2023-08-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SysNoticeEventListener {
    private final SysNoticeService sysNoticeService;

    /**
     * 新增通知 事件处理方法
     *
     * @param sysNoticeDto 敏感日志对象
     */
    @EventListener
    @Async
    public void recordSysNotice(SysNoticeDto sysNoticeDto) {
        sysNoticeService.saveUpdate(sysNoticeDto);
    }

}
