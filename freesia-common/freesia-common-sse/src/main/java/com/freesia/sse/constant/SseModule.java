package com.freesia.sse.constant;

import com.freesia.constant.SysModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 登录模块 静态类
 * @date 2024-01-13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SseModule extends SysModule {
    /**
     * 主模块 SSE管理模块
     */
    public static final String SSE_MANAGEMENT = "sse_management";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SubModule {
    }
}
