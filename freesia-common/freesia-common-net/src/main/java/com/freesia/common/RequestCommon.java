package com.freesia.common;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @Description 公共静态类
 * @author Evad.Wu
 * @date 2022-07-06
 */
@SuppressWarnings(value = "unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestCommon {
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

    public static final String BASE_PO_CREATOR = "creator";
    public static final String BASE_PO_CREATE_TIME = "createTime";
    public static final String BASE_PO_MODIFIER = "modifier";
    public static final String BASE_PO_MODIFY_TIME = "modifyTime";
    public static final String BASE_PO_LOGIC_DEL = "logicDel";
    public static final String BASE_PO_REC_VER = "recVer";

    public static final String CACHE_RANDOM_USER_AGENT = "cache-random-user-agent";

    public static final String SUFFIX_TOTAL_PAGE = "_total_page";
    public static final String SUFFIX_CURRENT_PAGE = "_current_page";
}
