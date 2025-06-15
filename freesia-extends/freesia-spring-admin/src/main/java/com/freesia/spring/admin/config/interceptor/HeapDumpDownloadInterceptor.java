package com.freesia.spring.admin.config.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Evad.Wu
 * @Description Actuator/heapdump请求处理 拦截器
 * @date 2025-06-16
 */
@Component
public class HeapDumpDownloadInterceptor implements ClientHttpRequestInterceptor {
    @NonNull
    @Override
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            @NonNull byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        if (request.getURI().getPath().contains("heapdump")) {
            response.getHeaders().set("Content-Type", "application/octet-stream");
            response.getHeaders().set("Content-Disposition", "attachment; filename=heapdump.hprof");
        }
        return response;
    }
}