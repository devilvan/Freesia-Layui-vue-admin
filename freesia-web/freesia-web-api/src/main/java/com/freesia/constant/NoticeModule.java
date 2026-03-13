package com.freesia.constant;

import com.freesia.constant.SysModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 消息提醒模块 静态类
 * @date 2025-09-20
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoticeModule extends SysModule {
    /**
     * 主模块 消息提醒模块
     */
    public static final String NOTICE_MANAGEMENT = "notice_management";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SubModule {
    }
}
