package com.freesia.net.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.freesia.constant.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.http.Consts;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Evad.Wu
 * @Description 客户端 工具类
 * @date 2023-08-13
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UServlet {
    /**
     * localhost IPv6
     */
    public static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    public static final String LOCALHOST = "127.0.0.1";
    private static final String UNKNOWN = "unknown";
    private static final String[] IP_HEADER_CANDIDATES = new String[]{
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    /**
     * 获取客户端IP地址, 模拟 hutool 5.8.x ServletUtil.getClientIP 行为, 但使用 jakarta.servlet
     *
     * @param request HttpServletRequest
     * @return 客户端IP地址
     */
    public static String getClientIP(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (StrUtil.isNotBlank(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
                // X-Forwarded-For 可能包含多个IP, 第一个为客户端真实IP
                int commaIndex = ip.indexOf(',');
                if (commaIndex > 0) {
                    ip = ip.substring(0, commaIndex);
                }
                return ip.trim();
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取当前请求的请求方式
     *
     * @return 当前请求的请求方式
     */
    public static String getMethod() {
        HttpServletRequest request = getRequest();
        if (ObjectUtil.isNotNull(request)) {
            return request.getMethod();
        }
        return null;
    }

    /**
     * 通过 {@link UriComponentsBuilder} 生成一个带参数的get请求URL
     *
     * @param url    原字符串
     * @param params get请求参数
     * @return 带参数的get请求URL
     */
    public static String generateHttpParamUrlByUriBuilder(String url, Map<String, Object> params) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        params.forEach(uriComponentsBuilder::queryParam);
        return uriComponentsBuilder.encode().toUriString();
    }

    /**
     * 生成一个带参数的get请求URL
     *
     * @param url    原字符串
     * @param params get请求参数
     * @return 带参数的get请求URL
     */
    public static String generateHttpParamUrl(String url, Map<String, Object> params) {
        List<BasicNameValuePair> basicNameValuePairList = new ArrayList<>();
        Set<Map.Entry<String, Object>> entrySet = params.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            basicNameValuePairList.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        //转换为键值对
        String str = null;
        try {
            str = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(basicNameValuePairList, Consts.UTF_8.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取请求方使用的浏览器
     *
     * @return 请求方使用的浏览器
     */
    public static String getBrowser() {
        UserAgent userAgent = getRequestUserAgent();
        if (userAgent != null) {
            if (userAgent.getOs() != null) {
                return userAgent.getBrowser().toString();
            }
        }
        return null;
    }

    /**
     * 获取请求方的操作系统
     *
     * @return 请求方的操作系统
     */
    public static String getOs() {
        UserAgent userAgent = getRequestUserAgent();
        if (userAgent != null) {
            if (userAgent.getOs() != null) {
                return userAgent.getOs().toString();
            }
        }
        return null;
    }

    /**
     * 获取当前请求头中的User-Agent
     *
     * @return UserAgent对象
     */
    public static UserAgent getRequestUserAgent() {
        if (getRequest() != null) {
            return UserAgentUtil.parse(getRequest().getHeader(Constants.USER_AGENT));
        }
        return null;
    }

    /**
     * 获取发起请求的IP地址
     *
     * @return 发起请求的IP地址
     */
    public static String getInitiatedRequestIp() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            String clientIp = getClientIP(request);
            if (LOCALHOST_IPV6.equals(clientIp)) {
                return LOCALHOST;
            }
            return clientIp;
        }
        return null;
    }

    /**
     * 获取请求的URI
     *
     * @return 请求的URI
     */
    public static String getRequestUri() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            if (request.getRequestURL() != null) {
                return request.getRequestURI();
            }
        }
        return null;
    }

    /**
     * 获取请求的URL
     *
     * @return 请求的URL
     */
    public static String getRequestUrl() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            if (request.getRequestURL() != null) {
                return request.getRequestURL().toString();
            }
        }
        return null;
    }

    /**
     * 获取httpResponse对象
     *
     * @return httpResponse对象
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = getRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getResponse();
        }
        return null;
    }

    /**
     * 获取httpRequest对象
     *
     * @return httpRequest对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = getRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getRequest();
        }
        return null;
    }

    /**
     * 获取客户端请求参数集对象
     *
     * @return 请求参数集对象
     */
    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return (ServletRequestAttributes) attributes;
        }
        return null;
    }
}
