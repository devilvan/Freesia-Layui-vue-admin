package com.freesia.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 预算日期类型 枚举类
 * @date 2025-03-06
 */
@Getter
@AllArgsConstructor
public enum BudgetType {
    /**
     * 日预算
     */
    DAY("DAY", "日预算"),
    /**
     * 周预算
     */
    WEEK("WEEK", "周预算"),
    /**
     * 月预算
     */
    MONTH("MONTH", "月预算"),
    /**
     * 年预算
     */
    YEAR("YEAR", "年预算"),
    /**
     * 自定义
     */
    CUSTOM("CUSTOM", "");

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
    public static BudgetType getInstanceByCode(String code) {
        BudgetType[] budgetTypes = BudgetType.values();
        for (BudgetType budgetType : budgetTypes) {
            if (budgetType.code.equals(code)) {
                return budgetType;
            }
        }
        return null;
    }
}
