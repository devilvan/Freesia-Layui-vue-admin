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
    EXPENSE("EXPENSES", "支出"),
    INCOME("INCOME", "收入");

    /**
     * 编码
     */
    public String code;
    /**
     * 描述
     */
    public String desc;
}
