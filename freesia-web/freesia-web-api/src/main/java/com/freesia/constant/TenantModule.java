package com.freesia.constant;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 租户模块 静态类
 * @date 2026-02-07
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantModule extends SysModule {
    /**
     * 主模块 租户管理模块
     */
    public static final String TENANT_MANAGEMENT = "tenant_management";

    @Data
    public static class SubModule {
        /**
         * 子模块 获取租户ID
         */
        public static final String GET_TENANT = "get_tenantId";
    }
}
