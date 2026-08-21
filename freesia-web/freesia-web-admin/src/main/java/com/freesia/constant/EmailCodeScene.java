package com.freesia.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 邮箱验证码使用场景
 */
@Getter
@AllArgsConstructor
public enum EmailCodeScene {
    REGISTER("register"),
    RESET_PASSWORD("reset_password");

    private final String code;

    public static EmailCodeScene from(String scene) {
        if (scene == null) {
            return null;
        }
        for (EmailCodeScene value : values()) {
            if (value.code.equalsIgnoreCase(scene)) {
                return value;
            }
        }
        return null;
    }
}
