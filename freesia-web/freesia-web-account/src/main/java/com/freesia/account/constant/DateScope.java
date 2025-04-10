package com.freesia.account.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Evad.Wu
 * @Description 折线图-根据时间查询 日期范围 枚举类
 * @date 2025-01-23
 */
@Getter
@AllArgsConstructor
public enum DateScope {
    /**
     * 周
     */
    WEEK("WEEK", "周"),
    /**
     * 月
     */
    MONTH("MONTH", "月"),
    /**
     * 年
     */
    YEAR("YEAR", "年");

    /**
     * 日期范围编码
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
    public static DateScope getInstanceByCode(String code) {
        DateScope[] dateScopes = DateScope.values();
        for (DateScope dateScope : dateScopes) {
            if (dateScope.code.equals(code)) {
                return dateScope;
            }
        }
        return null;
    }
}
