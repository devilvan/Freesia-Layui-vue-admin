package com.freesia.constant;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 公共静态类
 * @date 2022-07-06
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String WWW = "www.";
    /**
     * 反序列化时才有用
     */
    public static final String YM = "yyyy-MM";
    public static final String YMD = "yyyy-MM-dd";
    public static final String YMD_PATH = "yyyy/MM/dd";
    public static final String YMD_HM = "yyyy-MM-dd HH:mm";
    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YMD_HMS_SSS = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final SimpleDateFormat SDF_YMD = new SimpleDateFormat(Constants.YMD);
    public static final SimpleDateFormat SDF_YMD_PATH = new SimpleDateFormat(Constants.YMD_PATH);
    public static final SimpleDateFormat SDF_YMDHMS = new SimpleDateFormat(Constants.YMD_HMS);
    /**
     * 令牌
     */
    public static final String TOKEN = "token";
    /**
     * User-Agent
     */
    public static final String USER_AGENT = "User-Agent";
    /**
     * User-Agent
     */
    public static final String X_TENANT_ID = "X-Tenant-Id";
    /**
     * 验证码过期时间（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 2;
    /**
     * 加密
     */
    public static final String ENCRYPT = "encrypt";
    /**
     * UTF-8
     */
    public static final String UTF_8 = "UTF-8";
    /**
     * UTF8
     */
    public static final String UTF8 = "UTF8";

}
