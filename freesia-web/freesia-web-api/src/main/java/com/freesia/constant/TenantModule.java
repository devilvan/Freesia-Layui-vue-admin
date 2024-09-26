package com.freesia.constant;

import lombok.AccessLevel;
import lombok.Data;
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

    @Data
    public static class SubModule {
        /**
         * 子模块 保存租户
         */
        public static final String SAVE_TENANT = "save_tenant";
        /**
         * 子模块 删除租户
         */
        public static final String DELETE_TENANT = "delete_tenant";
        /**
         * 子模块 分配用户
         */
        public static final String ASSIGN_USER = "assign_user";
        /**
         * 子模块 取消分配用户
         */
        public static final String CANCEL_ASSIGN_USER = "cancel_assign_user";

    }
}
