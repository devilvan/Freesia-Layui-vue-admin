//package com.freesia.filter;
//
//import com.freesia.config.XssProperties;
//import jakarta.servlet.ReadListener;
//import jakarta.servlet.ServletInputStream;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
///**
// * @author Evad.Wu
// * @Description XSS请求包装器单元测试
// * @date 2026-08-11
// */
//@DisplayName("XssHttpServletRequestWrapper 请求包装测试")
//class XssHttpServletRequestWrapperTest {
//
//    private HttpServletRequest mockRequest;
//    private XssProperties properties;
//
//    @BeforeEach
//    void setUp() {
//        mockRequest = mock(HttpServletRequest.class);
//        properties = new XssProperties();
//        properties.setEnabled(true);
//        properties.setStrict(false);
//        properties.setFilterBody(true);
//        properties.setFilterHeaders(true);
//    }
//
//    @Nested
//    @DisplayName("参数过滤测试")
//    class ParameterFilterTest {
//
//        @Test
//        @DisplayName("应清洗参数中的 script 标签")
//        void shouldSanitizeScriptInParameter() {
//            Map<String, String[]> paramMap = new HashMap<>();
//            paramMap.put("name", new String[]{"<script>alert('XSS')</script>"});
//            when(mockRequest.getParameterMap()).thenReturn(paramMap);
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//
//            String result = wrapper.getParameter("name");
//            assertNotNull(result);
//            assertFalse(result.contains("<script"));
//            assertFalse(result.contains("alert("));
//        }
//
//        @Test
//        @DisplayName("应清洗参数中的 img onerror")
//        void shouldSanitizeImgOnerrorInParameter() {
//            Map<String, String[]> paramMap = new HashMap<>();
//            paramMap.put("avatar", new String[]{"<img src=x onerror=alert('XSS')>"});
//            when(mockRequest.getParameterMap()).thenReturn(paramMap);
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//
//            String result = wrapper.getParameter("avatar");
//            assertNotNull(result);
//            assertFalse(result.contains("onerror"));
//        }
//
//        @Test
//        @DisplayName("应清洗参数中的 javascript: 协议")
//        void shouldSanitizeJavascriptProtocolInParameter() {
//            Map<String, String[]> paramMap = new HashMap<>();
//            paramMap.put("url", new String[]{"javascript:alert('XSS')"});
//            when(mockRequest.getParameterMap()).thenReturn(paramMap);
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//
//            String result = wrapper.getParameter("url");
//            assertNotNull(result);
//            assertFalse(result.contains("javascript:"));
//        }
//
//        @Test
//        @DisplayName("应保留参数中的安全 HTML")
//        void shouldPreserveSafeHtmlInParameter() {
//            Map<String, String[]> paramMap = new HashMap<>();
//            paramMap.put("content", new String[]{"<p>Hello <strong>World</strong></p>"});
//            when(mockRequest.getParameterMap()).thenReturn(paramMap);
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//
//            String result = wrapper.getParameter("content");
//            assertNotNull(result);
//            assertTrue(result.contains("<p>"));
//            assertTrue(result.contains("<strong>"));
//        }
//
//        @Test
//        @DisplayName("应清洗多值参数")
//        void shouldSanitizeMultipleValues() {
//            Map<String, String[]> paramMap = new HashMap<>();
//            paramMap.put("tags", new String[]{"<script>alert(1)</script>", "正常", "<img src=x onerror=alert(2)>"});
//            when(mockRequest.getParameterMap()).thenReturn(paramMap);
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//
//            String[] result = wrapper.getParameterValues("tags");
//            assertNotNull(result);
//            assertEquals(3, result.length);
//            assertFalse(result[0].contains("<script"));
//            assertEquals("正常", result[1]);
//            assertFalse(result[2].contains("onerror"));
//        }
//
//        @Test
//        @DisplayName("getParameterMap 应返回清洗后的参数")
//        void shouldReturnSanitizedParameterMap() {
//            Map<String, String[]> paramMap = new HashMap<>();
//            paramMap.put("q", new String[]{"<script>alert(1)</script>"});
//            when(mockRequest.getParameterMap()).thenReturn(paramMap);
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//
//            Map<String, String[]> resultMap = wrapper.getParameterMap();
//            assertNotNull(resultMap);
//            String[] values = resultMap.get("q");
//            assertNotNull(values);
//            assertFalse(values[0].contains("<script"));
//        }
//
//        @Test
//        @DisplayName("getParameterNames 应返回正确的键集")
//        void shouldReturnCorrectParameterNames() {
//            Map<String, String[]> paramMap = new HashMap<>();
//            paramMap.put("a", new String[]{"1"});
//            paramMap.put("b", new String[]{"2"});
//            when(mockRequest.getParameterMap()).thenReturn(paramMap);
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//
//            Enumeration<String> names = wrapper.getParameterNames();
//            List<String> nameList = new ArrayList<>();
//            while (names.hasMoreElements()) {
//                nameList.add(names.nextElement());
//            }
//            assertTrue(nameList.contains("a"));
//            assertTrue(nameList.contains("b"));
//        }
//    }
//
//    @Nested
//    @DisplayName("请求头过滤测试")
//    class HeaderFilterTest {
//
//        @Test
//        @DisplayName("应清洗请求头中的 XSS")
//        void shouldSanitizeXssInHeader() {
//            when(mockRequest.getHeaderNames()).thenReturn(Collections.enumeration(Arrays.asList("X-Custom-Header")));
//            when(mockRequest.getHeader("X-Custom-Header")).thenReturn("<script>alert('XSS')</script>");
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//
//            String result = wrapper.getHeader("X-Custom-Header");
//            assertNotNull(result);
//            assertFalse(result.contains("<script"));
//        }
//
//        @Test
//        @DisplayName("排除列表中的请求头不应被清洗")
//        void shouldNotSanitizeExcludedHeaders() {
//            properties.setExcludeHeaders(Collections.singletonList("X-Auth-Token"));
//            when(mockRequest.getHeaderNames()).thenReturn(Collections.enumeration(Arrays.asList("X-Auth-Token")));
//            when(mockRequest.getHeader("X-Auth-Token")).thenReturn("<script>token</script>");
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//
//            String result = wrapper.getHeader("X-Auth-Token");
//            assertEquals("<script>token</script>", result);
//        }
//
//        @Test
//        @DisplayName("filterHeaders=false 时不应清洗请求头")
//        void shouldNotSanitizeWhenFilterHeadersDisabled() {
//            properties.setFilterHeaders(false);
//            when(mockRequest.getHeaderNames()).thenReturn(Collections.enumeration(Arrays.asList("X-Custom")));
//            when(mockRequest.getHeader("X-Custom")).thenReturn("<script>alert(1)</script>");
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//
//            String result = wrapper.getHeader("X-Custom");
//            assertEquals("<script>alert(1)</script>", result);
//        }
//    }
//
//    @Nested
//    @DisplayName("请求体过滤测试")
//    class BodyFilterTest {
//
//        @Test
//        @DisplayName("应清洗 JSON 请求体中的 XSS")
//        void shouldSanitizeXssInJsonBody() throws IOException {
//            String jsonBody = "{\"name\":\"<script>alert('XSS')</script>\",\"age\":25}";
//            byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
//            when(mockRequest.getContentType()).thenReturn("application/json");
//            when(mockRequest.getInputStream()).thenReturn(new ServletInputStream() {
//                private final ByteArrayInputStream bis = new ByteArrayInputStream(bodyBytes);
//
//                @Override
//                public boolean isFinished() {
//                    return bis.available() == 0;
//                }
//
//                @Override
//                public boolean isReady() {
//                    return true;
//                }
//
//                @Override
//                public void setReadListener(ReadListener readListener) {
//                    throw new UnsupportedOperationException();
//                }
//
//                @Override
//                public int read() {
//                    return bis.read();
//                }
//            });
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//            byte[] resultBytes = wrapper.getInputStream().readAllBytes();
//            String result = new String(resultBytes, StandardCharsets.UTF_8);
//
//            assertFalse(result.contains("<script"));
//            assertFalse(result.contains("alert("));
//            assertTrue(result.contains("\"name\":"));
//            assertTrue(result.contains("\"age\":25"));
//        }
//
//        @Test
//        @DisplayName("非文本类型请求体不应被过滤")
//        void shouldNotFilterNonTextBody() throws IOException {
//            String body = "binary data";
//            byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
//            when(mockRequest.getContentType()).thenReturn("multipart/form-data");
//            when(mockRequest.getInputStream()).thenReturn(new ServletInputStream() {
//                private final ByteArrayInputStream bis = new ByteArrayInputStream(bodyBytes);
//
//                @Override
//                public boolean isFinished() {
//                    return bis.available() == 0;
//                }
//
//                @Override
//                public boolean isReady() {
//                    return true;
//                }
//
//                @Override
//                public void setReadListener(ReadListener readListener) {
//                    throw new UnsupportedOperationException();
//                }
//
//                @Override
//                public int read() {
//                    return bis.read();
//                }
//            });
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//            byte[] resultBytes = wrapper.getInputStream().readAllBytes();
//            String result = new String(resultBytes, StandardCharsets.UTF_8);
//
//            assertEquals(body, result);
//        }
//
//        @Test
//        @DisplayName("filterBody=false 时不应过滤请求体")
//        void shouldNotFilterBodyWhenDisabled() throws IOException {
//            properties.setFilterBody(false);
//            String body = "<script>alert(1)</script>";
//            byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
//            when(mockRequest.getContentType()).thenReturn("application/json");
//            when(mockRequest.getInputStream()).thenReturn(new ServletInputStream() {
//                private final ByteArrayInputStream bis = new ByteArrayInputStream(bodyBytes);
//
//                @Override
//                public boolean isFinished() {
//                    return bis.available() == 0;
//                }
//
//                @Override
//                public boolean isReady() {
//                    return true;
//                }
//
//                @Override
//                public void setReadListener(ReadListener readListener) {
//                    throw new UnsupportedOperationException();
//                }
//
//                @Override
//                public int read() {
//                    return bis.read();
//                }
//            });
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//            byte[] resultBytes = wrapper.getInputStream().readAllBytes();
//            String result = new String(resultBytes, StandardCharsets.UTF_8);
//
//            assertEquals(body, result);
//        }
//
//        @Test
//        @DisplayName("getReader 应返回清洗后的内容")
//        void shouldReturnSanitizedReader() throws IOException {
//            String jsonBody = "{\"content\":\"<img src=x onerror=alert(1)>\"}";
//            byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
//            when(mockRequest.getContentType()).thenReturn("application/json");
//            when(mockRequest.getCharacterEncoding()).thenReturn("UTF-8");
//            when(mockRequest.getInputStream()).thenReturn(new ServletInputStream() {
//                private final ByteArrayInputStream bis = new ByteArrayInputStream(bodyBytes);
//
//                @Override
//                public boolean isFinished() {
//                    return bis.available() == 0;
//                }
//
//                @Override
//                public boolean isReady() {
//                    return true;
//                }
//
//                @Override
//                public void setReadListener(ReadListener readListener) {
//                    throw new UnsupportedOperationException();
//                }
//
//                @Override
//                public int read() {
//                    return bis.read();
//                }
//            });
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//            StringBuilder sb = new StringBuilder();
//            try (var reader = wrapper.getReader()) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line);
//                }
//            }
//
//            String result = sb.toString();
//            assertFalse(result.contains("onerror"));
//            assertFalse(result.contains("alert("));
//        }
//    }
//
//    @Nested
//    @DisplayName("严格模式测试")
//    class StrictModeTest {
//
//        @Test
//        @DisplayName("严格模式应移除所有 HTML 标签")
//        void strictModeShouldRemoveAllTags() {
//            properties.setStrict(true);
//            Map<String, String[]> paramMap = new HashMap<>();
//            paramMap.put("content", new String[]{"<p>Hello <script>alert(1)</script></p>"});
//            when(mockRequest.getParameterMap()).thenReturn(paramMap);
//
//            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(mockRequest, properties);
//
//            String result = wrapper.getParameter("content");
//            assertNotNull(result);
//            assertFalse(result.contains("<"));
//            assertFalse(result.contains(">"));
//        }
//    }
//}
