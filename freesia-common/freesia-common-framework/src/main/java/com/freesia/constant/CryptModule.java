package com.freesia.constant;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 加密/解密 模块类
 * @date 2024-03-19
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CryptModule extends SysModule {
    /**
     * 主模块 加密/解密管理模块
     */
    public static final String CRYPT_MANAGEMENT = "crypt_management";

    /**
     * 子模块
     */
    @Data
    public static class SubModule {
        /**
         * 子模块 加密
         */
        public static final String ENCRYPT = "encrypt";
        /**
         * 子模块 解密
         */
        public static final String DECRYPT = "decrypt";
    }
}
