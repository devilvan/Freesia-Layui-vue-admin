package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 标志位 静态类
 * @date 2023-08-12
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FlagConstant {
    /* AccountStatus、 DeptStatus*/
    /**
     * 启用
     */
    public static final String ENABLED = "0";
    /**
     * 禁用
     */
    public static final String DISABLED = "1";
    /* AccountStatus、 DeptStatus*/
    /**
     * 是
     */
    public static final String Y = "Y";
    /**
     * 否
     */
    public static final String N = "N";
    /**
     * 成功
     */
    public static final String SUCCESS = "success";
    /**
     * 失败
     */
    public static final String FAILED = "failed";
    /**
     * 真
     */
    public static final String TRUE = "true";
    /**
     * 假
     */
    public static final String FALSE = "FALSE";
}
