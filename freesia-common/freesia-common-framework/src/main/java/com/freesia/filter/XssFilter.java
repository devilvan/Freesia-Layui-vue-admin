package com.freesia.filter;

import cn.hutool.core.util.StrUtil;
import com.freesia.config.XssProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description XSS跨站脚本攻击过滤器
 * @date 2026-08-11
 */
public class XssFilter extends OncePerRequestFilter {
    private final XssProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public XssFilter(XssProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        XssHttpServletRequestWrapper wrappedRequest = new XssHttpServletRequestWrapper(request, properties);
        filterChain.doFilter(wrappedRequest, response);
    }

    private boolean isExcluded(HttpServletRequest request) {
        List<String> excludeUrls = properties.getExcludeUrls();
        if (excludeUrls == null || excludeUrls.isEmpty()) {
            return false;
        }
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String requestPath = StrUtil.removePrefix(requestUri, contextPath);
        if (StrUtil.isBlank(requestPath)) {
            requestPath = "/";
        }
        for (String pattern : excludeUrls) {
            if (pathMatcher.match(pattern, requestPath)) {
                return true;
            }
        }
        return false;
    }
}
