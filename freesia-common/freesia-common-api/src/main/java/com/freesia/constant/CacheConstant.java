package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 缓存键 静态类
 * 缓存组名称常量
 * key 格式为 cacheNames#ttl#maxIdleTime#maxSize
 * ttl 过期时间 如果设置为0则不过期 默认为0
 * maxIdleTime 最大空闲时间 根据LRU算法清理空闲数据 如果设置为0则不检测 默认为0
 * maxSize 组最大长度 根据LRU算法清理溢出数据 如果设置为0则无限长 默认为0
 * 例子: test#60s、test#0#60s、test#0#1m#1000、test#1h#0#500
 * @date 2023-08-12
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheConstant {
    /* 数据字典*/
    /**
     * 系统配置
     */
    public static final String SYS_CONFIG = "sys_config";
    /**
     * 数据字典
     */
    public static final String SYS_DICT = "sys_dict";
    /* 数据字典*/

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_code:";
    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";
    /**
     * 获取默认对象存储配置信息-缓存键
     */
    public static final String SYS_OSS_DEFAULT_CONFIG = "sys_oss:default_config";
    /**
     * 获取对象存储配置信息-缓存键
     */
    public static final String SYS_OSS_CONFIG = "sys_oss:cache_key";
    /**
     * OSS对象存储-缓存键
     */
    public static final String SYS_OSS = "sys_oss#30d";
    /**
     * 后端RSA公钥 缓存建
     */
    public static final String CRYPT_PUB = "crypt_pub1";
    /**
     * 后端RSA私钥 缓存建
     */
    public static final String CRYPT_PRI = "crypt_pri1";
    /**
     * 当前AES密钥（Base64串） 缓存键
     */
    public static final String CRYPT_AES = "crypt_aes";
    /**
     * 当前AES密钥生成时间戳（epoch millis） 缓存键
     */
    public static final String CRYPT_AES_TS = "crypt_aes_ts";
    /**
     * 宽限期内的上一把AES密钥（Base64串） 缓存键
     */
    public static final String CRYPT_AES_PREVIOUS = "crypt_aes_prev";
    /**
     * 防止重复提交 缓存建
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";
    /**
     * 用户自定义图标 缓存键
     */
    public static final String FIND_CACHE_COST_TYPE = "findCacheCostType:";
    /**
     * 记账报表-租户数据最早时间 缓存键
     */
    public static final String FIND_CACHE_ACCOUNT_COST_EARLY_PAYMENT_TIME = "findCacheAccountCostEarlyPaymentTime:";
    /**
     * 记账模块-查询预算 缓存键
     */
    public static final String FIND_BUDGET = "findBudget:";
    /**
     * 管理模块-查询自定义列头 缓存键
     */
    public static final String SYS_COLUMN_HEADER = "sys_column_header";
    /**
     * 管理模块-查询自定义列明细 缓存键
     */
    public static final String SYS_COLUMN_DETAIL = "sys_column_detail";
    /**
     * 管理模块-查询自定义列中间表 缓存键
     */
    public static final String SYS_COLUMN_MIDDLE = "sys_column_middle";
    /**
     * 管理模块-查询部门树 缓存键
     */
    public static final String DEFAULT_DEPT = "default_dept";
    /**
     * 管理模块-查询默认角色 缓存键
     */
    public static final String DEFAULT_ROLE = "default_role";
    /**
     * 管理模块-查询默认通用图标模板头表 缓存键
     */
    public static final String DEFAULT_COMMON_ICON_HEADER = "default_common_icon_header";
    /**
     * 管理模块-查询默认通用图标模板明细 缓存键
     */
    public static final String DEFAULT_COMMON_ICON_DETAIL = "default_common_icon_detail";
}
