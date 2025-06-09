package com.freesia.notice.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 通知/公告 枚举类
 * @date 2025-06-08
 */
@Getter
@AllArgsConstructor
public enum SysNotice {
    /**
     * 通知
     */
    NOTICE("NOTICE", "通知"),
    /**
     * 公告
     */
    ANNOUNCEMENT("ANNOUNCEMENT", "公告");

    private final String code;
    private final String desc;

    /**
     * 根据编码获取枚举对象
     *
     * @param code 编码
     * @return 枚举对象
     */
    public static SysNotice getInstanceByCode(String code) {
        SysNotice[] values = SysNotice.values();
        for (SysNotice value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
    }
