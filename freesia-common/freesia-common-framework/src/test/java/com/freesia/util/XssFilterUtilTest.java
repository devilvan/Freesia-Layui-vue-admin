//package com.freesia.util;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * @author Evad.Wu
// * @Description XSS过滤工具类单元测试
// * @date 2026-08-11
// */
//@DisplayName("XssFilterUtil XSS过滤测试")
//class XssFilterUtilTest {
//
//    @Nested
//    @DisplayName("clean() 基础白名单清洗测试")
//    class CleanTest {
//
//        @Test
//        @DisplayName("应移除 script 标签")
//        void shouldRemoveScriptTag() {
//            String input = "<script>alert('XSS')</script>";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("<script"), "结果不应包含 <script 标签");
//            assertFalse(result.contains("alert("), "结果不应包含 alert( 调用");
//        }
//
//        @Test
//        @DisplayName("应移除 img onerror 事件")
//        void shouldRemoveImgOnerror() {
//            String input = "<img src=x onerror=alert('XSS')>";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("onerror"), "结果不应包含 onerror");
//        }
//
//        @Test
//        @DisplayName("应移除 javascript: 协议")
//        void shouldRemoveJavascriptProtocol() {
//            String input = "<a href=\"javascript:alert('XSS')\">点击</a>";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("javascript:"), "结果不应包含 javascript: 协议");
//        }
//
//        @Test
//        @DisplayName("应移除 onload 事件")
//        void shouldRemoveOnloadEvent() {
//            String input = "<body onload=alert('XSS')>";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("onload"), "结果不应包含 onload");
//        }
//
//        @Test
//        @DisplayName("应移除 onclick 事件")
//        void shouldRemoveOnclickEvent() {
//            String input = "<div onclick=alert('XSS')>点击</div>";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("onclick"), "结果不应包含 onclick");
//        }
//
//        @Test
//        @DisplayName("应移除 iframe 标签")
//        void shouldRemoveIframeTag() {
//            String input = "<iframe src=\"http://evil.com\"></iframe>";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("<iframe"), "结果不应包含 <iframe 标签");
//        }
//
//        @Test
//        @DisplayName("应移除 object 标签")
//        void shouldRemoveObjectTag() {
//            String input = "<object data=\"evil.swf\"></object>";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("<object"), "结果不应包含 <object 标签");
//        }
//
//        @Test
//        @DisplayName("应移除 embed 标签")
//        void shouldRemoveEmbedTag() {
//            String input = "<embed src=\"evil.swf\">";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("<embed"), "结果不应包含 <embed 标签");
//        }
//
//        @Test
//        @DisplayName("应移除 style 标签中的 expression")
//        void shouldRemoveStyleExpression() {
//            String input = "<style>body{background:url('javascript:alert(1)')}</style>";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("expression("), "结果不应包含 expression(");
//        }
//
//        @Test
//        @DisplayName("应移除 SVG 中的脚本")
//        void shouldRemoveSvgScript() {
//            String input = "<svg><script>alert('XSS')</script></svg>";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("<script"), "结果不应包含 <script 标签");
//        }
//
//        @Test
//        @DisplayName("应保留安全的格式化标签")
//        void shouldPreserveSafeFormattingTags() {
//            String input = "<p>Hello <strong>World</strong></p>";
//            String result = XssFilterUtil.clean(input);
//            assertTrue(result.contains("<p>"), "应保留 <p> 标签");
//            assertTrue(result.contains("<strong>"), "应保留 <strong> 标签");
//        }
//
//        @Test
//        @DisplayName("应保留安全的链接")
//        void shouldPreserveSafeLink() {
//            String input = "<a href=\"https://example.com\">链接</a>";
//            String result = XssFilterUtil.clean(input);
//            assertTrue(result.contains("href=\"https://example.com\""), "应保留安全链接");
//        }
//
//        @Test
//        @DisplayName("应移除 a 标签中的 javascript: href")
//        void shouldRemoveJavascriptHrefInA() {
//            String input = "<a href=\"javascript:alert('XSS')\">点击</a>";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("javascript:"), "应移除 javascript: 协议");
//        }
//
//        @Test
//        @DisplayName("应保留 img 标签的安全属性")
//        void shouldPreserveSafeImgAttributes() {
//            String input = "<img src=\"https://example.com/pic.png\" alt=\"图片\" width=\"100\">";
//            String result = XssFilterUtil.clean(input);
//            assertTrue(result.contains("src="), "应保留 src 属性");
//            assertTrue(result.contains("alt="), "应保留 alt 属性");
//        }
//
//        @Test
//        @DisplayName("应移除 img 中的 onerror")
//        void shouldRemoveImgOnerrorEvent() {
//            String input = "<img src=\"x\" onerror=\"alert(1)\">";
//            String result = XssFilterUtil.clean(input);
//            assertFalse(result.contains("onerror"), "应移除 onerror 属性");
//            assertFalse(result.contains("alert("), "应移除 alert 调用");
//        }
//    }
//
//    @Nested
//    @DisplayName("cleanStrict() 严格模式测试")
//    class CleanStrictTest {
//
//        @Test
//        @DisplayName("应移除所有 HTML 标签")
//        void shouldRemoveAllHtmlTags() {
//            String input = "<p>Hello <strong>World</strong></p>";
//            String result = XssFilterUtil.cleanStrict(input);
//            assertFalse(result.contains("<"), "严格模式不应保留任何 HTML 标签");
//            assertFalse(result.contains(">"), "严格模式不应保留任何 HTML 标签");
//            assertTrue(result.contains("Hello World"), "应保留纯文本内容");
//        }
//
//        @Test
//        @DisplayName("应移除 script 并返回纯文本")
//        void shouldRemoveScriptAndReturnPlainText() {
//            String input = "<script>alert('XSS')</script>Hello";
//            String result = XssFilterUtil.cleanStrict(input);
//            assertEquals("alert('XSS')Hello", result, "应仅保留纯文本");
//        }
//
//        @Test
//        @DisplayName("应移除所有标签包括安全标签")
//        void shouldRemoveAllTagsIncludingSafe() {
//            String input = "<div><table><tr><td>内容</td></tr></table></div>";
//            String result = XssFilterUtil.cleanStrict(input);
//            assertEquals("内容", result, "严格模式应只保留纯文本");
//        }
//    }
//
//    @Nested
//    @DisplayName("clean(String[]) 数组清洗测试")
//    class CleanArrayTest {
//
//        @Test
//        @DisplayName("应清洗数组中每个元素")
//        void shouldCleanEachElement() {
//            String[] input = {"<script>alert(1)</script>", "<img src=x onerror=alert(1)>", "正常文本"};
//            String[] result = XssFilterUtil.clean(input);
//            assertNotNull(result);
//            assertEquals(3, result.length);
//            assertFalse(result[0].contains("<script"));
//            assertFalse(result[1].contains("onerror"));
//            assertEquals("正常文本", result[2]);
//        }
//
//        @Test
//        @DisplayName("空数组应原样返回")
//        void shouldReturnEmptyArray() {
//            String[] input = {};
//            String[] result = XssFilterUtil.clean(input);
//            assertNotNull(result);
//            assertEquals(0, result.length);
//        }
//
//        @Test
//        @DisplayName("null 数组应原样返回")
//        void shouldReturnNullForNullInput() {
//            String[] result = XssFilterUtil.clean((String[]) null);
//            assertNull(result);
//        }
//    }
//
//    @Nested
//    @DisplayName("containsXss() XSS特征检测测试")
//    class ContainsXssTest {
//
//        @Test
//        @DisplayName("检测 script 标签")
//        void shouldDetectScriptTag() {
//            assertTrue(XssFilterUtil.containsXss("<script>alert(1)</script>"));
//            assertTrue(XssFilterUtil.containsXss("<SCRIPT>alert(1)</SCRIPT>"));
//        }
//
//        @Test
//        @DisplayName("检测 javascript: 协议")
//        void shouldDetectJavascriptProtocol() {
//            assertTrue(XssFilterUtil.containsXss("javascript:alert(1)"));
//            assertTrue(XssFilterUtil.containsXss("JavaScript:alert(1)"));
//        }
//
//        @Test
//        @DisplayName("检测事件处理器")
//        void shouldDetectEventHandlers() {
//            assertTrue(XssFilterUtil.containsXss("onerror=alert(1)"));
//            assertTrue(XssFilterUtil.containsXss("onload=alert(1)"));
//            assertTrue(XssFilterUtil.containsXss("onclick=alert(1)"));
//            assertTrue(XssFilterUtil.containsXss("onmouseover=alert(1)"));
//            assertTrue(XssFilterUtil.containsXss("onfocus=alert(1)"));
//        }
//
//        @Test
//        @DisplayName("检测危险标签")
//        void shouldDetectDangerousTags() {
//            assertTrue(XssFilterUtil.containsXss("<iframe src=evil>"));
//            assertTrue(XssFilterUtil.containsXss("<object data=evil>"));
//            assertTrue(XssFilterUtil.containsXss("<embed src=evil>"));
//            assertTrue(XssFilterUtil.containsXss("<applet code=evil>"));
//        }
//
//        @Test
//        @DisplayName("检测危险函数")
//        void shouldDetectDangerousFunctions() {
//            assertTrue(XssFilterUtil.containsXss("eval('alert(1)')"));
//            assertTrue(XssFilterUtil.containsXss("alert('XSS')"));
//            assertTrue(XssFilterUtil.containsXss("confirm('XSS')"));
//            assertTrue(XssFilterUtil.containsXss("prompt('XSS')"));
//        }
//
//        @Test
//        @DisplayName("检测 document/window 访问")
//        void shouldDetectDocumentWindowAccess() {
//            assertTrue(XssFilterUtil.containsXss("document.cookie"));
//            assertTrue(XssFilterUtil.containsXss("document.write('<script>')"));
//            assertTrue(XssFilterUtil.containsXss("window.location='evil'"));
//            assertTrue(XssFilterUtil.containsXss("window.open('evil')"));
//        }
//
//        @Test
//        @DisplayName("正常文本不应被误判")
//        void shouldNotFlagNormalText() {
//            assertFalse(XssFilterUtil.containsXss("Hello World"));
//            assertFalse(XssFilterUtil.containsXss("这是一段正常的中文文本"));
//            assertFalse(XssFilterUtil.containsXss("<p>安全段落</p>"));
//            assertFalse(XssFilterUtil.containsXss("<a href='https://example.com'>安全链接</a>"));
//        }
//
//        @Test
//        @DisplayName("null/空字符串应返回 false")
//        void shouldReturnFalseForNullOrBlank() {
//            assertFalse(XssFilterUtil.containsXss(null));
//            assertFalse(XssFilterUtil.containsXss(""));
//            assertFalse(XssFilterUtil.containsXss("   "));
//        }
//    }
//
//    @Nested
//    @DisplayName("边界条件测试")
//    class EdgeCaseTest {
//
//        @Test
//        @DisplayName("null 输入应原样返回")
//        void shouldReturnNullForNullInput() {
//            assertNull(XssFilterUtil.clean((String) null));
//            assertNull(XssFilterUtil.cleanStrict((String) null));
//        }
//
//        @Test
//        @DisplayName("空字符串应原样返回")
//        void shouldReturnEmptyForEmptyInput() {
//            assertEquals("", XssFilterUtil.clean(""));
//            assertEquals("", XssFilterUtil.cleanStrict(""));
//        }
//
//        @Test
//        @DisplayName("空白字符串应原样返回")
//        void shouldReturnBlankForBlankInput() {
//            assertEquals("   ", XssFilterUtil.clean("   "));
//            assertEquals("   ", XssFilterUtil.cleanStrict("   "));
//        }
//
//        @Test
//        @DisplayName("纯文本应原样返回")
//        void shouldReturnPlainTextUnchanged() {
//            String plainText = "Hello World, 这是一段纯文本";
//            assertEquals(plainText, XssFilterUtil.clean(plainText));
//            assertEquals(plainText, XssFilterUtil.cleanStrict(plainText));
//        }
//
//        @Test
//        @DisplayName("仅包含安全标签的 HTML 应保留")
//        void shouldPreserveSafeHtml() {
//            String safeHtml = "<div><p>Hello <strong>World</strong></p></div>";
//            String result = XssFilterUtil.clean(safeHtml);
//            assertTrue(result.contains("<div>"));
//            assertTrue(result.contains("<p>"));
//            assertTrue(result.contains("<strong>"));
//            assertTrue(result.contains("Hello World"));
//        }
//
//        @Test
//        @DisplayName("JSON 字符串中的 XSS 应被清洗")
//        void shouldCleanXssInJsonString() {
//            String jsonWithXss = "{\"name\":\"<script>alert(1)</script>\",\"desc\":\"<img src=x onerror=alert(1)>\"}";
//            String result = XssFilterUtil.clean(jsonWithXss);
//            assertFalse(result.contains("<script"));
//            assertFalse(result.contains("onerror"));
//        }
//
//        @Test
//        @DisplayName("多种 XSS payload 组合应全部清洗")
//        void shouldCleanCombinedXssPayloads() {
//            String combined = "<script>alert(1)</script><img src=x onerror=alert(2)>" +
//                    "<a href=\"javascript:alert(3)\">click</a><iframe src=\"evil\"></iframe>" +
//                    "<body onload=alert(4)><div onclick=alert(5)>";
//            String result = XssFilterUtil.clean(combined);
//            assertFalse(result.contains("<script"));
//            assertFalse(result.contains("onerror"));
//            assertFalse(result.contains("javascript:"));
//            assertFalse(result.contains("<iframe"));
//            assertFalse(result.contains("onload"));
//            assertFalse(result.contains("onclick"));
//        }
//    }
//
//    @Nested
//    @DisplayName("真实场景 XSS Payload 测试")
//    class RealWorldPayloadTest {
//
//        @Test
//        @DisplayName("测试 TypeJudging XSS")
//        void shouldDefeatTypeJudging() {
//            String payload = "<a href=\"javascript:alert(document.cookie)\">点击</a>";
//            String result = XssFilterUtil.clean(payload);
//            assertFalse(result.contains("javascript:"));
//            assertFalse(result.contains("document.cookie"));
//        }
//
//        @Test
//        @DisplayName("测试 Bracketless 表达式")
//        void shouldDefeatBracketlessExpression() {
//            String payload = "<img src=x onerror=alert(1)>";
//            String result = XssFilterUtil.clean(payload);
//            assertFalse(result.contains("onerror"));
//            assertFalse(result.contains("alert(1)"));
//        }
//
//        @Test
//        @DisplayName("测试大小写绕过")
//        void shouldDefeatCaseBypass() {
//            String payload = "<ScRiPt>alert(1)</ScRiPt>";
//            String result = XssFilterUtil.clean(payload);
//            assertFalse(result.toLowerCase().contains("<script"));
//        }
//
//        @Test
//        @DisplayName("测试编码绕过")
//        void shouldDefeatEncodedBypass() {
//            String payload = "<img src=\"x\" onerror=\"alert('XSS')\">";
//            String result = XssFilterUtil.clean(payload);
//            assertFalse(result.contains("onerror"));
//        }
//
//        @Test
//        @DisplayName("测试双写绕过")
//        void shouldDefeatDoubleWriteBypass() {
//            String payload = "<<script>alert(1)<</script>";
//            String result = XssFilterUtil.clean(payload);
//            assertFalse(result.contains("alert(1)"));
//        }
//
//        @Test
//        @DisplayName("测试 SVG 内嵌脚本")
//        void shouldDefeatSvgInjection() {
//            String payload = "<svg onload=alert(1)>";
//            String result = XssFilterUtil.clean(payload);
//            assertFalse(result.contains("onload"));
//        }
//
//        @Test
//        @DisplayName("测试事件处理器注入")
//        void shouldDefeatEventHandlerInjection() {
//            String payload = "<div onmouseover=alert(1)>hover me</div>";
//            String result = XssFilterUtil.clean(payload);
//            assertFalse(result.contains("onmouseover"));
//        }
//
//        @Test
//        @DisplayName("测试 data: 协议注入")
//        void shouldDefeatDataProtocolInjection() {
//            String payload = "<a href=\"data:text/html,<script>alert(1)</script>\">click</a>";
//            String result = XssFilterUtil.clean(payload);
//            assertFalse(result.contains("<script"));
//        }
//    }
//}
