package com.freesia.net.builder;

import com.freesia.net.constant.HttpContentType;
import com.freesia.net.dto.HttpClientDto;
import com.freesia.net.exception.HttpClientException;
import com.freesia.net.util.UServlet;
import com.freesia.util.UEmpty;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Evad.Wu
 * @Description HttpClientComponent改造的Http请求建造器
 * @date 2022-11-21
 */
@Slf4j
public class HttpBuilder {
    private HttpRequestBase httpRequest;
    private Map<String, String> headers;
    private CredentialsProvider credentialsProvider;

    protected HttpBuilder() {
        super();
    }

    /**
     * 根据请求方式+url，生成HttpRequest对象
     *
     * @param requestMethod 请求方式 {@link RequestMethod}
     * @param url           请求路径
     * @return 返回请求对象
     */
    private static HttpRequestBase requestMethod2HttpRequestBase(RequestMethod requestMethod, String url) {
        return HttpContentType.methodApply(requestMethod, url);
    }

    /**
     * 步骤一：创建一个建造器对象
     *
     * @return 建造器对象
     */
    public static HttpBuilder create() {
        return new HttpBuilder();
    }

    /**
     * 步骤二：根据建造器收集的成员对象参数，构造一个 {@link HttpBuilder} 对象
     *
     * @return HttpClient对象
     */
    public HttpClientDto build() {
        return new HttpClientDto(this.httpRequest, this.headers, this.credentialsProvider);
    }

    /**
     * 给建造器添加请求对象（无参数/无请求体）
     *
     * @param requestMethod 请求类型
     * @param url           请求地址
     * @return 建造器对象
     */
    public final HttpBuilder setHttpRequest(RequestMethod requestMethod, String url) {
        this.httpRequest = Optional.ofNullable(requestMethod2HttpRequestBase(requestMethod, url))
                .orElseThrow(() -> new HttpClientException("http.request.type.invalid", new Object[]{requestMethod.name()}));
        return this;
    }

    /**
     * 给建造器添加请求对象（带参数/请求体）
     *
     * @param requestMethod 请求类型
     * @param url           请求地址
     * @param param         参数/请求体
     * @param <T>           参数/请求体类型
     * @return 建造器对象
     */
    public final <T> HttpBuilder setHttpRequest(RequestMethod requestMethod, String url, T param) {
        this.httpRequest = Optional.ofNullable(requestMethod2HttpRequestBase(requestMethod, url))
                .orElseThrow(() -> new HttpClientException("request.type.invalid", new Object[]{}));
        this.checkSetHttpParam(requestMethod).accept(param);
        return this;
    }

    /**
     * 1. 判断请求类型是否支持请求头
     * 2. 根据消费的参数类型决定添加param还是requestBody
     *
     * @param requestMethod 请求类型
     * @param <T>           入参的类型
     * @return 一个消费者接口
     */
    @SuppressWarnings("unchecked")
    private <T> Consumer<T> checkSetHttpParam(RequestMethod requestMethod) {
        return t -> {
            if (t instanceof Map) {
                Map<String, Object> paramMap = (Map<String, Object>) t;
                if (UEmpty.isNotEmpty(paramMap)) {
                    String build = UServlet.generateHttpParamUrlByUriBuilder(this.httpRequest.getURI().toString(), paramMap);
                    this.httpRequest.setURI(URI.create(build));
                }
            } else if (t instanceof String) {
                // 判断请求类型是否支持请求体
                if (!isSupportRequestBody(requestMethod)) {
                    throw new HttpClientException("http.request.type.body.not.support", new Object[]{requestMethod.name()});
                }
                String requestBody = t.toString();
                if (UEmpty.isNotEmpty(requestBody)) {
                    StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                    HttpEntityEnclosingRequestBase httpRequest1 = (HttpEntityEnclosingRequestBase) this.httpRequest;
                    httpRequest1.setEntity(entity);
                    this.httpRequest = httpRequest1;
                }
            }
        };
    }

    /**
     * 判断请求类型是否支持请求体
     *
     * @param requestMethod 请求类型
     * @return 是否支持请求体
     */
    private boolean isSupportRequestBody(RequestMethod requestMethod) {
        return RequestMethod.POST.equals(requestMethod) ||
                RequestMethod.PUT.equals(requestMethod) ||
                RequestMethod.PATCH.equals(requestMethod);
    }

    /**
     * 给建造器设置头信息
     *
     * @param headers 头信息
     * @return 建造器对象
     */
    public final HttpBuilder setHeaders(Map<String, String> headers) {
        if (UEmpty.isNotEmpty(headers)) {
            this.headers = headers;
            Set<Map.Entry<String, String>> entrySet = headers.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                this.httpRequest.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }
}
