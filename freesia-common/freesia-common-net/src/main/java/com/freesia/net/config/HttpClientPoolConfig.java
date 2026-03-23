package com.freesia.net.config;

import com.freesia.net.handler.HttpClientRetryHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Evad.Wu
 * @Description HttpClient连接池组件
 * @date 2022-07-07
 */
@Slf4j
@Configuration
public class HttpClientPoolConfig {
    @Bean
    public PoolingHttpClientConnectionManager getHttpClientPool() {
        //创建连接池管理器
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        //设置最大连接数
        connectionManager.setMaxTotal(1024);
        //设置每个主机的最大连接数
        connectionManager.setDefaultMaxPerRoute(64);
        return connectionManager;
    }

    @Bean
    public RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                // 从连接池获取连接的timeout
                .setConnectionRequestTimeout(1000)
                // 客户端与服务端建立连接的timeout
                .setConnectTimeout(6000)
                // 建立连接后传输数据的timeout
                .setSocketTimeout(6000)
                .build();
    }

    @Bean
    public HttpClientRetryHandler httpClientRetryHandler() {
        return new HttpClientRetryHandler();
    }
}
