package com.freesia.account.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 开支类型 枚举类
 * @date 2025-01-20
 */
@Getter
@AllArgsConstructor
public enum CostType {
    /**
     * 支出
     */
    EXPENSE("EXPENSES", "支出"),
    /**
     * 收入
     */
    INCOME("INCOME", "收入");

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
    public static CostType getInstanceByCode(String code) {
        CostType[] values = CostType.values();
        for (CostType value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
