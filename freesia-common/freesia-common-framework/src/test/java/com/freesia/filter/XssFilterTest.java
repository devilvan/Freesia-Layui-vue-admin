package com.freesia.filter;

import com.freesia.config.XssProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Evad.Wu
 * @Description XSS过滤器集成测试
 * @date 2026-08-11
 */
@DisplayName("XssFilter 过滤器集成测试")
class XssFilterTest {

    private XssProperties properties;
    private XssFilter xssFilter;

    @BeforeEach
    void setUp() {
        properties = new XssProperties();
        properties.setEnabled(true);
        properties.setStrict(false);
        properties.setFilterBody(true);
        properties.setFilterHeaders(false);
        properties.setExcludeUrls(Collections.emptyList());
        xssFilter = new XssFilter(properties);
    }

    @Nested
    @DisplayName("过滤器启用/禁用测试")
    class EnableDisableTest {

        @Test
        @DisplayName("启用时应包装请求")
        void shouldWrapRequestWhenEnabled() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("GET");
            request.setRequestURI("/api/test");
            request.addParameter("name", "<script>alert(1)</script>");

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            xssFilter.doFilter(request, response, filterChain);

            assertNotNull(capturedRequest[0], "应传递包装后的请求");
            assertInstanceOf(XssHttpServletRequestWrapper.class, capturedRequest[0],
                    "应为 XssHttpServletRequestWrapper 实例");

            String sanitizedName = capturedRequest[0].getParameter("name");
            assertFalse(sanitizedName.contains("<script"), "参数应被清洗");
        }

        @Test
        @DisplayName("禁用时不应包装请求")
        void shouldNotWrapRequestWhenDisabled() throws ServletException, IOException {
            properties.setEnabled(false);
            XssFilter disabledFilter = new XssFilter(properties);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("GET");
            request.setRequestURI("/api/test");
            request.addParameter("name", "<script>alert(1)</script>");

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            disabledFilter.doFilter(request, response, filterChain);

            assertSame(request, capturedRequest[0], "禁用时应传递原始请求");
        }
    }

    @Nested
    @DisplayName("排除URL测试")
    class ExcludeUrlTest {

        @Test
        @DisplayName("排除URL列表中的路径不应被过滤")
        void shouldSkipExcludedUrl() throws ServletException, IOException {
            properties.setExcludeUrls(Collections.singletonList("/api/upload/**"));
            XssFilter filter = new XssFilter(properties);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("POST");
            request.setRequestURI("/api/upload/file");
            request.addParameter("content", "<script>alert(1)</script>");

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            filter.doFilter(request, response, filterChain);

            assertSame(request, capturedRequest[0], "排除URL不应被过滤");
        }

        @Test
        @DisplayName("非排除URL应被过滤")
        void shouldFilterNonExcludedUrl() throws ServletException, IOException {
            properties.setExcludeUrls(Collections.singletonList("/api/upload/**"));
            XssFilter filter = new XssFilter(properties);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("GET");
            request.setRequestURI("/api/user/list");
            request.addParameter("q", "<script>alert(1)</script>");

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            filter.doFilter(request, response, filterChain);

            assertInstanceOf(XssHttpServletRequestWrapper.class, capturedRequest[0],
                    "非排除URL应被过滤");
        }

        @Test
        @DisplayName("通配符排除模式应正确匹配")
        void shouldMatchWildcardExcludePattern() throws ServletException, IOException {
            properties.setExcludeUrls(Collections.singletonList("/static/**"));
            XssFilter filter = new XssFilter(properties);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("GET");
            request.setRequestURI("/static/images/logo.png");
            request.addParameter("name", "<script>alert(1)</script>");

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            filter.doFilter(request, response, filterChain);

            assertSame(request, capturedRequest[0], "通配符排除URL不应被过滤");
        }
    }

    @Nested
    @DisplayName("XSS Payload 端到端过滤测试")
    class EndToEndTest {

        @Test
        @DisplayName("GET 参数中的 script 标签应被过滤")
        void shouldFilterScriptInGetParam() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("GET");
            request.setRequestURI("/api/user/search");
            request.addParameter("keyword", "<script>alert('XSS')</script>");

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            xssFilter.doFilter(request, response, filterChain);

            String sanitized = capturedRequest[0].getParameter("keyword");
            assertFalse(sanitized.contains("<script"), "script 标签应被移除");
        }

        @Test
        @DisplayName("GET 参数中的 img.onerror 应被过滤")
        void shouldFilterImgOnerrorInGetParam() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("GET");
            request.setRequestURI("/api/user/avatar");
            request.addParameter("url", "<img src=x onerror=alert('XSS')>");

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            xssFilter.doFilter(request, response, filterChain);

            String sanitized = capturedRequest[0].getParameter("url");
            assertFalse(sanitized.contains("onerror"), "onerror 事件应被移除");
        }

        @Test
        @DisplayName("GET 参数中的 javascript: 协议应被过滤")
        void shouldFilterJavascriptProtocolInGetParam() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("GET");
            request.setRequestURI("/api/link");
            request.addParameter("redirect", "javascript:alert(document.cookie)");

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            xssFilter.doFilter(request, response, filterChain);

            String sanitized = capturedRequest[0].getParameter("redirect");
            assertFalse(sanitized.contains("javascript:"), "javascript: 协议应被移除");
        }

        @Test
        @DisplayName("POST JSON 请求体中的 XSS 应被过滤")
        void shouldFilterXssInPostJsonBody() throws ServletException, IOException {
            String jsonBody = "{\"username\":\"<script>alert(1)</script>\",\"email\":\"<img src=x onerror=alert(2)>\",\"bio\":\"normal text\"}";
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("POST");
            request.setRequestURI("/api/user/create");
            request.setContentType("application/json");
            request.setCharacterEncoding("UTF-8");
            request.setContent(jsonBody.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            xssFilter.doFilter(request, response, filterChain);

            byte[] bodyBytes = capturedRequest[0].getInputStream().readAllBytes();
            String result = new String(bodyBytes, java.nio.charset.StandardCharsets.UTF_8);

            assertFalse(result.contains("<script"), "script 标签应被移除");
            assertFalse(result.contains("onerror"), "onerror 事件应被移除");
            assertTrue(result.contains("normal text"), "正常文本应保留");
        }

        @Test
        @DisplayName("正常参数应原样保留")
        void shouldPreserveNormalParams() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("GET");
            request.setRequestURI("/api/user/profile");
            request.addParameter("name", "张三");
            request.addParameter("age", "25");

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            xssFilter.doFilter(request, response, filterChain);

            assertEquals("张三", capturedRequest[0].getParameter("name"));
            assertEquals("25", capturedRequest[0].getParameter("age"));
        }

        @Test
        @DisplayName("GET 参数中的 iframe 标签应被过滤")
        void shouldFilterIframeInGetParam() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("GET");
            request.setRequestURI("/api/page/preview");
            request.addParameter("content", "<iframe src=\"http://evil.com\"></iframe>");

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            xssFilter.doFilter(request, response, filterChain);

            String sanitized = capturedRequest[0].getParameter("content");
            assertFalse(sanitized.contains("<iframe"), "iframe 标签应被移除");
        }

        @Test
        @DisplayName("多个 XSS 攻击向量应全部被过滤")
        void shouldFilterMultipleXssVectors() throws ServletException, IOException {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("GET");
            request.setRequestURI("/api/batch");
            request.addParameter("field1", "<script>alert(1)</script>");
            request.addParameter("field2", "<img src=x onerror=alert(2)>");
            request.addParameter("field3", "<a href=\"javascript:alert(3)\">click</a>");
            request.addParameter("field4", "正常内容");

            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);
            final HttpServletRequest[] capturedRequest = new HttpServletRequest[1];
            doAnswer(invocation -> {
                capturedRequest[0] = invocation.getArgument(0);
                return null;
            }).when(filterChain).doFilter(any());

            xssFilter.doFilter(request, response, filterChain);

            assertFalse(capturedRequest[0].getParameter("field1").contains("<script"));
            assertFalse(capturedRequest[0].getParameter("field2").contains("onerror"));
            assertFalse(capturedRequest[0].getParameter("field3").contains("javascript:"));
            assertEquals("正常内容", capturedRequest[0].getParameter("field4"));
        }
    }

    @Nested
    @DisplayName("auto-configuration 测试")
    class AutoConfigurationTest {

        @Test
        @DisplayName("XssAutoConfiguration 应正确注册 Bean")
        void shouldRegisterBeans() {
            XssAutoConfiguration config = new XssAutoConfiguration();
            XssFilter filter = config.xssFilter(properties);
            assertNotNull(filter);

            FilterRegistrationBean<XssFilter> registration = config.xssFilterRegistration(filter);
            assertNotNull(registration);
            assertEquals(1, registration.getUrlPatterns().size());
            assertEquals("/*", registration.getUrlPatterns().iterator().next());
            assertEquals("xssFilter", registration.getName());
        }

        @Test
        @DisplayName("配置属性应正确绑定")
        void shouldBindPropertiesCorrectly() {
            XssProperties props = new XssProperties();
            props.setEnabled(true);
            props.setStrict(true);
            props.setFilterBody(false);
            props.setFilterHeaders(true);
            props.setExcludeUrls(Collections.singletonList("/api/test/**"));
            props.setExcludeHeaders(Collections.singletonList("Authorization"));

            assertTrue(props.isEnabled());
            assertTrue(props.isStrict());
            assertFalse(props.isFilterBody());
            assertTrue(props.isFilterHeaders());
            assertEquals(1, props.getExcludeUrls().size());
            assertEquals("/api/test/**", props.getExcludeUrls().get(0));
            assertEquals(1, props.getExcludeHeaders().size());
            assertEquals("Authorization", props.getExcludeHeaders().get(0));
        }
    }
}
