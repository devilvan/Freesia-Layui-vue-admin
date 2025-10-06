package com.freesia.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 数值计算 工具类
 * @date 2025-10-04
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UCalculate {
    /**
     * 对无法整除的精度数值进行整除分配
     *
     * @param total 精度数值
     * @param parts 分配数量
     * @return 分配后的整数精度数值
     */
    public static BigDecimal[] integerDivide(BigDecimal total, int parts) {
        BigDecimal[] result = new BigDecimal[parts];

        // 计算整数商和余数
        BigDecimal quotient = total.divideToIntegralValue(BigDecimal.valueOf(parts));
        BigDecimal remainder = total.remainder(BigDecimal.valueOf(parts));
        // 所有部分先赋值为商
        Arrays.fill(result, quotient);
        // 将余数分配到前几个部分（每个加1）
        int remainderInt = remainder.intValue();
        for (int i = 0; i < remainderInt; i++) {
            result[i] = result[i].add(BigDecimal.ONE);
        }
        return result;
    }

    /**
     * 精确小数分配法
     *
     * @param total 精度数值
     * @param parts 分配数量
     * @param scale 精度
     * @return 分配后的整数精度数值
     */
    public static BigDecimal[] preciseDivide(BigDecimal total, int parts, int scale) {
        BigDecimal[] result = new BigDecimal[parts];

        // 先计算前n-1份（向下取整）
        BigDecimal singlePart = total.divide(BigDecimal.valueOf(parts), scale, RoundingMode.DOWN);
        for (int i = 0; i < parts - 1; i++) {
            result[i] = singlePart;
        }

        // 最后一份用总数减去前n-1份的和
        BigDecimal sumOfPrevious = BigDecimal.ZERO;
        for (int i = 0; i < parts - 1; i++) {
            sumOfPrevious = sumOfPrevious.add(result[i]);
        }
        result[parts - 1] = total.subtract(sumOfPrevious);

        return result;
    }
}
