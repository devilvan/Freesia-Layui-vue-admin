package com.freesia.service;

import com.freesia.dto.OAuthLoginDto;

import java.util.Map;

/**
 * @author Bliss.Wu
 * @Description OAuth 登录服务接口
 * @date 2026-05-30
 */
public interface OAuthLoginService {

    /**
     * 构建授权页 URL 并返回给前端用于跳转
     *
     * @param provider    平台编码
     * @param redirectUrl 登录成功后前端回调地址
     * @return 授权页 URL
     */
    String buildAuthorizeUrl(String provider, String redirectUrl);

    /**
     * 处理 OAuth 回调：交换 code 获取 token → 获取用户信息 → 登录/注册 → 返回 Freesia token
     *
     * @param provider 平台编码
     * @param code     授权码
     * @param state    防 CSRF 状态码
     * @return token 和跳转地址 Map
     */
    Map<String, Object> handleCallback(String provider, String code, String state);

    /**
     * POST 方式 OAuth 登录（适用于小程序等不走重定向的场景）
     *
     * @param dto 登录请求
     * @return token 和跳转地址 Map
     */
    Map<String, Object> oauthLogin(OAuthLoginDto dto);
}
