package com.freesia.constant;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 系统配置模块 静态类
 * @date 2024-09-26
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigModule extends SysModule {
    /**
     * 主模块 系统配置管理模块
     */
    public static final String CONFIG_MANAGEMENT = "config_management";

    @Data
    public static class SubModule {
        /**
         * 子模块 删除系统配置
         */
        public static final String DELETE_CONFIG = "delete_config";
        /**
         * 子模块 保存系统配置
         */
        public static final String SAVE_CONFIG = "save_config";
    }
}
