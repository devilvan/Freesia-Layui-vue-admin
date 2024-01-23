package com.freesia.httpclient.constant;

import com.freesia.constant.SysModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description Http调用相关模块 静态类
 * @date 2024-01-18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpClientModule extends SysModule {
    /**
     * 主模块 Http调用模块
     */
    public static final String HTTP_CLIENT_MANAGEMENT = "http_client_management";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class SubModule {

    }
}
