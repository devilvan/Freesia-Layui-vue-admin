package com.freesia.service;

import com.freesia.constant.EmailCodeScene;

/**
 * 邮箱验证码服务
 */
public interface EmailAuthService {
    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱
     * @param scene 场景
     */
    void sendEmailCode(String email, EmailCodeScene scene);

    /**
     * 校验邮箱验证码
     *
     * @param email 邮箱
     * @param code  验证码
     * @param scene 场景
     */
    void validateEmailCode(String email, String code, EmailCodeScene scene);
}
