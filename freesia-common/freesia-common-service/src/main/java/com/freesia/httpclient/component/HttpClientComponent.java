package com.freesia.httpclient.component;

import com.freesia.httpclient.builder.HttpBuilder;
import com.freesia.constant.ExceptionConstant;
import com.freesia.httpclient.handler.HttpClientRetryHandler;
import com.freesia.httpclient.dto.HttpClientDto;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 生成HttpClient组件
 * @date 2022-07-06
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpClientComponent {
    private final PoolingHttpClientConnectionManager connectionManager;
    private final RequestConfig requestConfig;
    private final HttpClientRetryHandler httpClientRetryHandler;

    /**
     * 重构步骤三：通过 {@link HttpBuilder} 构建的httpclient提供请求方法对象以及
     *
     * @param httpClientDto {@link HttpBuilder} 构建的httpclient
     * @return 请求返回的报文
     */
    public String doExecute(HttpClientDto httpClientDto) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(httpClientRetryHandler)
                .setConnectionManager(connectionManager);
        CloseableHttpClient client = httpClientBuilder.build();
        String resJson = null;
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpClientDto.getHttpRequest());
            HttpEntity responseEntity = response.getEntity();
            resJson = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(ExceptionConstant.INTERNAL_SERVER_EXP.getMessage());
        } finally {
            try {
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resJson;
    }
}
