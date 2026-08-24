package com.freesia.scheduler;

import com.freesia.service.SysOssService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Bliss.Wu
 * @Description 删除临时文件定时任务
 * @date 2026-08-24
 */
@Component
@RequiredArgsConstructor
public class DeleteTempFileScheduler {
    private final SysOssService sysOssService;

    /**
     * 删除临时文件定时任务
     */
    @XxlJob("deleteTempFileTask")
    public ReturnT<String> deleteTempFileTask() {
        sysOssService.deleteTempFileTask();
        return ReturnT.SUCCESS;
    }
}
