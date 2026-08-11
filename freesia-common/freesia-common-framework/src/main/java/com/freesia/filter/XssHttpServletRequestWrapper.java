package com.freesia.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.freesia.config.XssProperties;
import com.freesia.util.XssFilterUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Evad.Wu
 * @Description XSS请求包装器，对请求参数、请求头、请求体进行XSS过滤
 * @date 2026-08-11
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private static final int MAX_BODY_SIZE = 1024 * 1024;

    private final XssProperties properties;
    private final Map<String, String[]> cachedParameterMap;
    private final Map<String, String> cachedHeaderMap;
    /**
     * 获取已缓存的请求体（可供业务层再次读取）
     */
    @Getter
    private byte[] cachedBody;

    public static final List<String> EXCLUDE_CONTENT_TYPES = List.of(
            "application/json", "application/xml", "text/xml", "text/plain", "text/html"
    );

    public XssHttpServletRequestWrapper(HttpServletRequest request, XssProperties properties) {
        super(request);
        this.properties = properties;
        this.cachedParameterMap = sanitizeParameterMap(request.getParameterMap());
        this.cachedHeaderMap = sanitizeHeaderMap(request);
    }

    private Map<String, String[]> sanitizeParameterMap(Map<String, String[]> original) {
        if (original == null || original.isEmpty()) {
            return original;
        }
        Map<String, String[]> sanitized = new HashMap<>(original.size());
        for (Map.Entry<String, String[]> entry : original.entrySet()) {
            String[] values = entry.getValue();
            String[] sanitizedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                sanitizedValues[i] = properties.isStrict()
                        ? XssFilterUtil.cleanStrict(values[i])
                        : XssFilterUtil.clean(values[i]);
            }
            sanitized.put(entry.getKey(), sanitizedValues);
        }
        return sanitized;
    }

    private Map<String, String> sanitizeHeaderMap(HttpServletRequest request) {
        Map<String, String> sanitized = new HashMap<>(16);
        if (!properties.isFilterHeaders()) {
            return sanitized;
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return sanitized;
        }
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (properties.getExcludeHeaders().contains(headerName)) {
                continue;
            }
            String headerValue = request.getHeader(headerName);
            if (headerValue != null) {
                sanitized.put(headerName, properties.isStrict()
                        ? XssFilterUtil.cleanStrict(headerValue)
                        : XssFilterUtil.clean(headerValue));
            }
        }
        return sanitized;
    }

    @Override
    public String getParameter(String name) {
        String[] values = cachedParameterMap.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return cachedParameterMap;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(cachedParameterMap.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return cachedParameterMap.get(name);
    }

    @Override
    public String getHeader(String name) {
        if (properties.isFilterHeaders() && cachedHeaderMap.containsKey(name)) {
            return cachedHeaderMap.get(name);
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (properties.isFilterHeaders() && cachedHeaderMap.containsKey(name)) {
            Vector<String> values = new Vector<>();
            values.add(cachedHeaderMap.get(name));
            return values.elements();
        }
        return super.getHeaders(name);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!properties.isFilterBody()) {
            return super.getInputStream();
        }
        if (cachedBody == null) {
            cachedBody = getBodyBytes();
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(cachedBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return bis.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read() {
                return bis.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        Charset charset = resolveCharset();
        if (cachedBody == null) {
            cachedBody = getBodyBytes();
        }
        return new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(cachedBody), charset));
    }

    private byte[] getBodyBytes() throws IOException {
        String contentType = getContentType();
        if (contentType == null) {
            return new byte[0];
        }
        String lowerContentType = contentType.toLowerCase();
        if (!EXCLUDE_CONTENT_TYPES.contains(lowerContentType)) {
            return new byte[0];
        }
        byte[] originalBody = IoUtil.readBytes(super.getInputStream());
        if (originalBody.length == 0) {
            return originalBody;
        }
        if (originalBody.length > MAX_BODY_SIZE) {
            return originalBody;
        }
        String bodyStr = new String(originalBody, resolveCharset());
        String sanitized = properties.isStrict()
                ? XssFilterUtil.cleanStrict(bodyStr)
                : XssFilterUtil.clean(bodyStr);
        return sanitized.getBytes(resolveCharset());
    }

    private Charset resolveCharset() {
        String encoding = getCharacterEncoding();
        if (StrUtil.isNotBlank(encoding)) {
            try {
                return Charset.forName(encoding);
            } catch (Exception ignored) {
            }
        }
        return StandardCharsets.UTF_8;
    }

}
