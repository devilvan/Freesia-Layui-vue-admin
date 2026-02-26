package com.freesia.account.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 记账模块 静态类
 * @date 2025-03-06
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountModule {
    /**
     * 主模块 记账模块
     */
    public static final String ACCOUNT_MANAGEMENT = "ACCOUNT_MANAGEMENT";
    /**
     * 主模块 记账-报表模块
     */
    public static final String ACCOUNT_REPORT_MANAGEMENT = "ACCOUNT_REPORT_MANAGEMENT";

    /**
     * 子模块
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public final static class SubModule {
    }
}
