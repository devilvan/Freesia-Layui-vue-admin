package com.freesia.sse.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.freesia.controller.BaseController;
import com.freesia.satoken.util.USecurity;
import com.freesia.sse.component.SseEmitterManager;
import com.freesia.sse.constant.SseTopic;
import com.freesia.sse.dto.SseMessageDto;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description SSE (Server-Sent Events) 控制器
 * @date 2024-10-22
 */
@CrossOrigin
@RestController
@ConditionalOnProperty(value = "sse.enabled", havingValue = "true")
@Tag(name = "SseController", description = "SSE (Server-Sent Events) 控制器")
public class SseController extends BaseController implements DisposableBean {
    @Resource
    private SseEmitterManager sseEmitterManager;

    /**
     * 建立 SSE 连接
     */
    @GetMapping(value = "${sse.path}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        Long userId = USecurity.getUserId();
        String token = USecurity.getToken();
        return sseEmitterManager.connect(userId, token);
    }

    /**
     * 关闭 SSE 连接
     */
    @SaIgnore
    @GetMapping(value = "${sse.path}/close")
    public R<Void> close() {
        String tokenValue = StpUtil.getTokenValue();
        Long userId = USecurity.getUserId();
        sseEmitterManager.disconnect(userId, tokenValue);
        return R.ok();
    }

    /**
     * 向特定用户发送消息
     *
     * @param userId 目标用户的 ID
     * @param msg    要发送的消息内容
     */
    @GetMapping(value = "${sse.path}/send")
    public R<Void> send(Long userId, String msg) {
        SseMessageDto sseMessageDto = new SseMessageDto();
        sseMessageDto.setUserIdList(List.of(userId));
        sseMessageDto.setTopicList(Collections.singletonList(SseTopic.GLOBAL_SSE.getKey()));
        sseMessageDto.setContent(msg);
        sseEmitterManager.publish(sseMessageDto);
        return R.ok();
    }

    /**
     * 向所有用户发送消息
     *
     * @param msg 要发送的消息内容
     */
    @GetMapping(value = "${sse.path}/sendAll")
    public R<Void> send(String msg) {
        SseMessageDto sseMessageDto = new SseMessageDto();
        sseMessageDto.setTopicList(Collections.singletonList(SseTopic.GLOBAL_SSE.getKey()));
        sseMessageDto.setContent(msg);
        sseEmitterManager.publishAll(sseMessageDto);
        return R.ok();
    }

    /**
     * 清理资源。此方法目前不执行任何操作，但避免因未实现而导致错误
     */
    @Override
    public void destroy() throws Exception {
        // 销毁时不需要做什么 此方法避免无用操作报错
    }

}
