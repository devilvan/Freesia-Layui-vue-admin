package com.freesia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 缓存的名字 静态类
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
    public static final String  PWD_ERR_CNT_KEY = "pwd_err_cnt:";
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
}
