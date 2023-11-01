package com.freesia.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 设备类型 枚举类
 * @date 2023-08-17
 */
@Getter
@AllArgsConstructor
public enum DeviceType {
    /**
     * PC端
     */
    PC("pc"),
    /**
     * APP端
     */
    APP("app"),
    /**
     * 小程序
     */
    APPLET("applet");

    private final String device;
}
