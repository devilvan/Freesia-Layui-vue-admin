package com.freesia.event;

import com.freesia.bean.SysSensitiveLogBean;
import com.freesia.dto.SysSensitiveLogDto;
import com.freesia.service.SysSensitiveLogService;
import com.freesia.util.UCopy;
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
public class SysSensitiveLogEventListener {
    private final SysSensitiveLogService sysSensitiveLogService;

    /**
     * 记录敏感操作日志 事件处理方法
     * 注：20230918 审计数据当前登录用户中获取后，会导致JPA保存失败，并报错WEB上下文问题
     * 解决办法：https://blog.csdn.net/m290345792/article/details/125068018
     *
     * @param sysSensitiveLogBean 敏感日志对象
     */
    @EventListener
    @Async("threadPoolTaskExecutor")
    public void recordLoginOperateLog(SysSensitiveLogBean sysSensitiveLogBean) {
        SysSensitiveLogDto sysSensitiveLogDto = new SysSensitiveLogDto();
        UCopy.fullCopy(sysSensitiveLogBean, sysSensitiveLogDto);
        sysSensitiveLogService.saveUpdate(sysSensitiveLogDto);
    }

}
