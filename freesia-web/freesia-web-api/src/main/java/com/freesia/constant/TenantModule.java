package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 租户管理 静态类
 * @date 2024-02-05
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantModule extends SysModule {
    /**
     * 主模块 租户管理
     */
    public static final String TENANT_MANAGEMENT = "tenant_management";
}
