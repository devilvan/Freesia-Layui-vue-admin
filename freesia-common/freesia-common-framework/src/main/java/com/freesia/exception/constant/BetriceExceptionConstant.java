package com.freesia.exception.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 自定义异常信息枚举类
 * @date 2022-07-21
 */
@Getter
@AllArgsConstructor
@SuppressWarnings(value = "unused")
public enum BetriceExceptionConstant {
    /**
     * 内部服务异常
     */
    INTERNAL_SERVER_EXP(500, "内部服务异常！"),
    /**
     * 枚举类型不存在
     */
    ENUMERATION_NOT_EXISTS_EXP(500, "枚举类型不存在！"),
    /**
     * 枚举类型不存在
     */
    JOB_PARAMS_NOT_NULL_EXP(500, "枚举类型不存在！"),
    /**
     * 被代理对象不能为空
     */
    PROXIED_IS_NULL_EXP(500, "被代理对象不能为空"),
    /**
     * 代理信息有误！
     */
    PROXY_INFO_INCORRECT_EXP(500, "代理信息有误！"),
    /**
     * 超过最大重试次数！
     */
    RETRY_MAXIMUN_TRIES_REACHED_EXP(400, "超过最大重试次数！"),
    /**
     * 线程第N次无响应！
     */
    RETRY_TIMES_NO_RESPONSE_EXP(400, "线程：{} 第{}次无响应！"),
    /**
     * 连接超时
     */
    RETRY_TIME_OUT_EXP(400, "连接超时！"),
    /**
     * 服务器拒绝连接
     */
    RETRY_CONNECTION_REFUSED_EXP(400, "服务器拒绝连接！"),
    /**
     * SSL握手失败
     */
    RETRY_SSL_HAND_SHAKE_EXP(400, "SSL握手失败！"),
    /**
     * 解析返回集合有误
     */
    PARSE_JSON_EXP(400, "解析返回集合有误！"),
    ;

    private final Integer code;
    private final String message;
}
