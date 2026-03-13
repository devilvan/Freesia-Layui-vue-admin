package com.freesia.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 消息类别 枚举类
 * @date 2025-09-19
 */
@Getter
@AllArgsConstructor
public enum SysNoticeCategory {
    /**
     * 记账
     */
    ACCOUNT("ACCOUNT", "记账");

    private final String code;
    private final String desc;

    /**
     * 根据编码获取枚举对象
     *
     * @param code 编码
     * @return 枚举对象
     */
    public static SysNoticeCategory getInstanceByCode(String code) {
        SysNoticeCategory[] values = SysNoticeCategory.values();
        for (SysNoticeCategory value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
