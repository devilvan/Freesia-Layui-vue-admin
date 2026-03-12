package com.freesia.strategy;

import com.freesia.util.UEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Bliss.Wu
 * @Description 授权策略
 * @date 2026-03-12
 */
public interface AuthStrategy {
    String NAME = "AuthStrategy";

    /**
     * @author Bliss.Wu
     * @Description 策略类型
     * @date 2026-03-12
     */
    @Getter
    @AllArgsConstructor
    enum Type {
        /**
         * 密码授权
         */
        PASSWORD("password", "密码授权"),
        /**
         * 邮箱授权
         */
        EMAIL("email", "邮箱授权"),
        /**
         * 扫码授权
         */
        QRCODE("qrcode", "二维码授权"),
        /**
         * 第三方授权
         */
        THIRD_PARTY("third_party", "第三方授权"),
        ;

        /**
         * 编码
         */
        private final String code;
        /**
         * 描述
         */
        private final String desc;

        /**
         * 根据编码获取枚举对象
         *
         * @param code 编码
         * @return 枚举对象
         */
        public static Type getInstanceByCode(String code) {
            if (UEmpty.isEmpty(code)) {
                return null;
            }
            for (Type value : values()) {
                if (value.getCode().equals(code)) {
                    return value;
                }
            }
            return null;
        }
    }
}
