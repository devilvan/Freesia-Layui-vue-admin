package com.freesia.satoken.constant;

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
    MIN_PROGRAM("minProgram"),
    /**
     * 第三方授权
     */
    THIRD_PARTY_AUTH("thirdPartyAuth"),
    ;

    private final String device;
}
