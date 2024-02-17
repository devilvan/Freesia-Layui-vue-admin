package com.freesia.httpclient.dto;

import com.freesia.httpclient.builder.HttpBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.Map;

/**
 * @author Evad.Wu
 * @Description 由 {@link HttpBuilder}构建的HttpClient对象
 * @date 2022-11-21
 */
@Slf4j
@Data
@AllArgsConstructor
public class HttpClientDto {
    /**
     * 请求对象
     */
    private HttpRequestBase httpRequest;
    /**
     * 请求对象的头信息
     */
    private Map<String, String> headers;
    /**
     * 代理对象的证书
     */
    private CredentialsProvider credentialsProvider;
}
