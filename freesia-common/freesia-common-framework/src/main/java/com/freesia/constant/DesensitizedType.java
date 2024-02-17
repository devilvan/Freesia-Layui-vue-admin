package com.freesia.constant;

/**
 * @author Evad.Wu
 * @Description 自定义脱敏数据类型 枚举类
 * @date 2023-03-12
 */
public enum DesensitizedType {
    /**
     * 默认无
     */
    NONE,
    /**
     * 用户id
     */
    USER_ID,
    /**
     * 中文名
     */
    CHINESE_NAME,
    /**
     * 外国人名
     */
    EURO_AMERICAN_NAME,
    /**
     * 身份证号
     */
    ID_CARD,
    /**
     * 座机号
     */
    FIXED_PHONE,
    /**
     * 手机号
     */
    MOBILE_PHONE,
    /**
     * 地址
     */
    ADDRESS,
    /**
     * 电子邮件
     */
    EMAIL,
    /**
     * 密码
     */
    PASSWORD,
    /**
     * 中国大陆车牌，包含普通车辆、新能源车辆
     */
    CAR_LICENSE,
    /**
     * 银行卡
     */
    BANK_CARD
}
