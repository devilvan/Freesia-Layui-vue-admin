package com.freesia.account.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 费用分摊状态 枚举类
 * @date 2025-10-12
 */
@Getter
@AllArgsConstructor
public enum AllocStatus {
    /**
     * 未分摊
     */
    UNFINISHED("UNFINISHED", "msg.unfinished"),
    /**
     * 部分分摊
     */
    PART("PART", "msg.part"),
    /**
     * 已分摊
     */
    FINISHED("FINISHED", "msg.finished");

    private final String code;
    private final String i18n;

    /**
     * 根据编码获取枚举对象
     *
     * @param code 编码
     * @return 枚举对象
     */
    public static AllocStatus getInstanceByCode(String code) {
        AllocStatus[] values = AllocStatus.values();
        for (AllocStatus allocStatus : values) {
            if (allocStatus.code.equals(code)) {
                return allocStatus;
            }
        }
        return null;
    }
}
