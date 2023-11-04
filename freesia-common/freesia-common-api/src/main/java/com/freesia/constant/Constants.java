package com.freesia.constant;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 公共静态类
 * @date 2022-07-06
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    public static final Integer INIT_TOTAL_PAGE = -1;
    public static final Integer INIT_CURRENT_PAGE = 1;

    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_REFERERED = "Referered";
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_COOKIE = "Cookie";

    public static final String CACHE_RANDOM_USER_AGENT = "cache-random-user-agent";

    public static final String SUFFIX_TOTAL_PAGE = "_total_page";
    public static final String SUFFIX_CURRENT_PAGE = "_current_page";

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String WWW = "www.";

    public static final String YMD = "yyyy-MM-dd";
    public static final String YMD_HM = "yyyy-MM-dd HH:mm";
    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YMD_HMS_SSS = "yyyy-MM-dd HH:mm:ss:SSS";

    /**
     * 令牌
     */
    public static final String TOKEN = "token";
    /**
     * User-Agent
     */
    public static final String USER_AGENT = "User-Agent";
    /**
     * 字符集 UTF_8
     */
    public static final String UTF_8 = "UTF-8";
    /**
     * 验证码过期时间（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 2;
}
