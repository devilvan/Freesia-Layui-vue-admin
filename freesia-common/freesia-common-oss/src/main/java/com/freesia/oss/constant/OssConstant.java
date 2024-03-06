package com.freesia.oss.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description OSS对象存储 静态类
 * @date 2024-02-27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OssConstant {
    /**
     * 获取默认对象存储配置信息-缓存键
     */
    public static final String SYS_OSS_DEFAULT_CONFIG = "sys_oss:default_config";
    /**
     * 获取对象存储配置信息-缓存键
     */
    public static final String SYS_OSS_CONFIG = "sys_oss:cache_key";
    /**
     * 云服务商
     */
    public static final String[] CLOUD_SERVICE = new String[]{"aliyun", "qcloud", "qiniu", "obs"};
    /**
     * OSS对象存储-缓存键
     */
    public static final String SYS_OSS = "sys_oss#30d";
}
