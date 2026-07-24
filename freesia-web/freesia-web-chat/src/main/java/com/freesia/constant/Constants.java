package com.freesia.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 对话模块 静态类
 * @date 2026-07-19
 */
public final class Constants {
    /**
     * 角色枚举
     */
    @Getter
    @AllArgsConstructor
    public enum Role {
        USER("user", "用户"),
        ASSISTANT("assistant", "助手"),
        SYSTEM("system", "系统");

        /**
         * 角色编码
         */
        private final String code;
        /**
         * 角色名称
         */
        private final String name;

        /**
         * 根据编码获取实例
         *
         * @param code 编码
         * @return 实例
         */
        public static Role getInstanceByCode(String code) {
            if (code == null) return null;
            for (Role role : Role.values()) {
                if (role.getCode().equals(code)) {
                    return role;
                }
            }
            return null;
        }
    }

    /**
     * 提供商枚举
     */
    @Getter
    @AllArgsConstructor
    public enum Provider {
        DEEPSEEK("deepseek", "Deepseek");

        /**
         * 提供商编码
         */
        private final String code;
        /**
         * 提供商名称
         */
        private final String name;

        /**
         * 根据编码获取实例
         *
         * @param code 编码
         * @return 实例
         */
        public static Provider getInstanceByCode(String code) {
            if (code == null) return null;
            for (Provider provider : Provider.values()) {
                if (provider.getCode().equals(code)) {
                    return provider;
                }
            }
            return null;
        }
    }
}
