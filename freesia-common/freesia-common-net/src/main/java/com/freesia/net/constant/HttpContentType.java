package com.freesia.net.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.client.methods.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.function.Function;

/**
 * @author Evad.Wu
 * @Description Http ContentType 枚举类
 * @date 2023-06-16
 */
@Getter
@AllArgsConstructor
public enum HttpContentType {
    /**
     * GET
     */
    GET(HttpGet::new),
    /**
     * POST
     */
    POST(HttpPost::new),
    /**
     * PUT
     */
    PUT(HttpPut::new),
    /**
     * DELETE
     */
    DELETE(HttpDelete::new),
    /**
     * HEAD
     */
    HEAD(HttpHead::new),
    /**
     * OPTIONS
     */
    OPTIONS(HttpOptions::new),
    /**
     * PATCH
     */
    PATCH(HttpPatch::new),
    /**
     * TRACE
     */
    TRACE(HttpTrace::new);

    private final Function<String, HttpRequestBase> method;

    public static HttpRequestBase methodApply(RequestMethod requestMethod, String url) {
        HttpContentType[] httpContentTypes = HttpContentType.values();
        for (HttpContentType httpContentType : httpContentTypes) {
            if (httpContentType.name().equals(requestMethod.name())) {
                return httpContentType.method.apply(url);
            }
        }
        return null;
    }
}
