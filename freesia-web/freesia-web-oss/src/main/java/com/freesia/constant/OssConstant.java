package com.freesia.constant;

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
     * 云服务商
     */
    public static final String[] CLOUD_SERVICE = new String[]{"aliyun", "qcloud", "qiniu", "obs"};

}
