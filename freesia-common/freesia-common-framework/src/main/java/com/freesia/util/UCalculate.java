package com.freesia.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 * @author Evad.Wu
 * @Description 数值计算 工具类
 * @date 2025-10-04
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UCalculate {
    // 常量定义
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    // 保留2位小数
    private static final int SCALE = 2;

    /**
     * 将一个保留2位小数的数字根据份数拆分
     *
     * @param total 总金额
     * @param parts 拆分份数
     * @return 拆分后的BigDecimal数组
     */
    public static BigDecimal[] split(BigDecimal total, int parts) {
        // 参数验证
        validateParameters(total, parts);

        // 转换为分进行计算
        BigDecimal totalCents = total.multiply(HUNDRED);

        // 计算每份的基本值和余数
        BigDecimal[] divideResult = totalCents.divideAndRemainder(BigDecimal.valueOf(parts));
        BigDecimal baseValue = divideResult[0];
        BigDecimal remainder = divideResult[1];

        BigDecimal[] result = new BigDecimal[parts];
        int remainderInt = remainder.intValue();

        // 生成拆分结果
        for (int i = 0; i < parts; i++) {
            if (i < remainderInt) {
                // 前remainder份多分配1分钱
                result[i] = baseValue.add(ONE).divide(HUNDRED, SCALE, ROUNDING_MODE);
            } else {
                // 剩余的份数分配基本值
                result[i] = baseValue.divide(HUNDRED, SCALE, ROUNDING_MODE);
            }
        }

        return result;
    }

    /**
     * 使用double类型参数的便捷方法
     *
     * @param total 总金额
     * @param parts 拆分份数
     * @return 拆分后的BigDecimal数组
     */
    public static BigDecimal[] split(double total, int parts) {
        return split(BigDecimal.valueOf(total), parts);
    }

    /**
     * 使用String类型参数的便捷方法（避免double精度问题）
     *
     * @param total 总金额字符串
     * @param parts 拆分份数
     * @return 拆分后的BigDecimal数组
     */
    public static BigDecimal[] split(String total, int parts) {
        return split(new BigDecimal(total), parts);
    }

    /**
     * 返回double数组格式的结果
     *
     * @param total 总金额
     * @param parts 拆分份数
     * @return 拆分后的double数组
     */
    public static double[] splitToDoubleArray(BigDecimal total, int parts) {
        BigDecimal[] bigDecimalResult = split(total, parts);
        return Arrays.stream(bigDecimalResult)
                .mapToDouble(BigDecimal::doubleValue)
                .toArray();
    }

    /**
     * 返回格式化字符串数组
     *
     * @param total 总金额
     * @param parts 拆分份数
     * @return 格式化后的字符串数组
     */
    public static String[] splitToStringArray(BigDecimal total, int parts) {
        BigDecimal[] result = split(total, parts);
        String[] formatted = new String[result.length];

        for (int i = 0; i < result.length; i++) {
            formatted[i] = result[i].setScale(SCALE, RoundingMode.HALF_UP).toString();
        }

        return formatted;
    }

    /**
     * 验证拆分结果的总和是否等于原总数
     *
     * @param total  原总数
     * @param result 拆分结果
     * @return 验证是否通过
     */
    public static boolean validateResult(BigDecimal total, BigDecimal[] result) {
        BigDecimal sum = Arrays.stream(result)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.compareTo(total) == 0;
    }

    /**
     * 获取拆分结果的详细信息
     *
     * @param total 总金额
     * @param parts 拆分份数
     * @return 包含详细信息的字符串
     */
    public static String getSplitDetails(BigDecimal total, int parts) {
        BigDecimal[] result = split(total, parts);
        StringBuilder sb = new StringBuilder();

        sb.append("拆分详情:\n");
        sb.append(String.format("总金额: %s\n", total.setScale(SCALE, ROUNDING_MODE)));
        sb.append(String.format("拆分份数: %d\n", parts));
        sb.append("拆分结果:\n");

        for (int i = 0; i < result.length; i++) {
            sb.append(String.format("  第%d份: %s\n", i + 1, result[i]));
        }

        sb.append(String.format("验证总和: %s\n",
                Arrays.stream(result).reduce(BigDecimal.ZERO, BigDecimal::add)));
        sb.append(String.format("验证结果: %s", validateResult(total, result) ? "✓ 通过" : "✗ 失败"));

        return sb.toString();
    }

    /**
     * 参数验证
     */
    private static void validateParameters(BigDecimal total, int parts) {
        if (total == null) {
            throw new IllegalArgumentException("总金额不能为null");
        }

        if (parts <= 0) {
            throw new IllegalArgumentException("份数必须是正整数");
        }

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("总金额不能为负数");
        }

        // 检查总金额的小数位数
        if (total.scale() > SCALE) {
            throw new IllegalArgumentException("总金额最多只能保留" + SCALE + "位小数");
        }
    }
}
