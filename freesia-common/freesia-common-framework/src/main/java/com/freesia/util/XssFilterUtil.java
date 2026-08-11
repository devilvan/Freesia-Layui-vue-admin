package com.freesia.util;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * @author Evad.Wu
 * @Description XSS过滤工具类
 * @date 2026-08-11
 */
@SuppressWarnings(value = "unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XssFilterUtil {
    /**
     * 基础白名单（允许常见格式化标签）
     */
    private static final Safelist BASIC_SAFELIST = Safelist.relaxed()
            .addTags("h1", "h2", "h3", "h4", "h5", "h6", "p", "br", "hr",
                    "span", "div", "table", "thead", "tbody", "tfoot", "tr", "td", "th",
                    "ul", "ol", "li", "dl", "dt", "dd",
                    "img", "video", "audio", "source",
                    "blockquote", "pre", "code", "figure", "figcaption",
                    "strong", "em", "b", "i", "u", "s", "sub", "sup",
                    "a", "font", "center", "label",
                    "input", "textarea", "select", "button")
            .addAttributes("a", "href", "target", "rel", "title", "class", "style")
            .addAttributes("img", "src", "alt", "width", "height", "class", "style")
            .addAttributes("video", "src", "controls", "width", "height", "poster", "preload", "class", "style")
            .addAttributes("audio", "src", "controls", "preload", "class", "style")
            .addAttributes("source", "src", "type")
            .addAttributes("font", "color", "size", "face")
            .addAttributes("input", "type", "name", "value", "placeholder", "disabled", "readonly", "maxlength")
            .addAttributes("textarea", "name", "placeholder", "disabled", "readonly", "maxlength", "rows", "cols")
            .addAttributes("select", "name", "disabled", "multiple", "size")
            .addAttributes("button", "type", "name", "value", "disabled")
            .addAttributes("td", "colspan", "rowspan", "width", "height", "class", "style")
            .addAttributes("th", "colspan", "rowspan", "width", "height", "class", "style")
            .addAttributes("span", "class", "style")
            .addAttributes("div", "class", "style")
            .addProtocols("a", "href", "http", "https", "mailto", "tel")
            .addProtocols("img", "src", "http", "https", "data")
            .addProtocols("video", "src", "http", "https", "data")
            .addProtocols("audio", "src", "http", "https", "data")
            .addProtocols("source", "src", "http", "https", "data");

    /**
     * 严格白名单（仅保留纯文本，移除所有HTML标签）
     */
    private static final Safelist STRICT_SAFELIST = Safelist.none();

    /**
     * 使用Jsoup清洗XSS
     *
     * @param value 待清洗的字符串
     * @return 清洗后的字符串
     */
    public static String clean(String value) {
        if (CharSequenceUtil.isBlank(value)) {
            return value;
        }
        return Jsoup.clean(value, BASIC_SAFELIST);
    }

    /**
     * 使用严格模式清洗（移除所有HTML标签）
     *
     * @param value 待清洗的字符串
     * @return 清洗后的纯文本字符串
     */
    public static String cleanStrict(String value) {
        if (CharSequenceUtil.isBlank(value)) {
            return value;
        }
        return Jsoup.clean(value, STRICT_SAFELIST);
    }

    /**
     * 使用自定义白名单清洗
     *
     * @param value   待清洗的字符串
     * @param safelist 自定义白名单
     * @return 清洗后的字符串
     */
    public static String clean(String value, Safelist safelist) {
        if (CharSequenceUtil.isBlank(value) || safelist == null) {
            return value;
        }
        return Jsoup.clean(value, safelist);
    }

    /**
     * 对字符串数组进行XSS清洗
     *
     * @param values 待清洗的字符串数组
     * @return 清洗后的字符串数组
     */
    public static String[] clean(String[] values) {
        if (values == null || values.length == 0) {
            return values;
        }
        String[] cleaned = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleaned[i] = clean(values[i]);
        }
        return cleaned;
    }

    /**
     * 对字符串数组进行严格模式清洗
     *
     * @param values 待清洗的字符串数组
     * @return 清洗后的字符串数组
     */
    public static String[] cleanStrict(String[] values) {
        if (values == null || values.length == 0) {
            return values;
        }
        String[] cleaned = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleaned[i] = cleanStrict(values[i]);
        }
        return cleaned;
    }

    /**
     * 检测字符串是否包含XSS攻击特征
     *
     * @param value 待检测的字符串
     * @return 是否包含XSS攻击特征
     */
    public static boolean containsXss(String value) {
        if (CharSequenceUtil.isBlank(value)) {
            return false;
        }
        String lower = value.toLowerCase();
        return lower.contains("<script")
                || lower.contains("javascript:")
                || lower.contains("onerror=")
                || lower.contains("onload=")
                || lower.contains("onclick=")
                || lower.contains("onmouseover=")
                || lower.contains("onfocus=")
                || lower.contains("onblur=")
                || lower.contains("onchange=")
                || lower.contains("onsubmit=")
                || lower.contains("onreset=")
                || lower.contains("onselect=")
                || lower.contains("onkeydown=")
                || lower.contains("onkeyup=")
                || lower.contains("onmousedown=")
                || lower.contains("onmouseup=")
                || lower.contains("onmousemove=")
                || lower.contains("onmouseout=")
                || lower.contains("onmouseenter=")
                || lower.contains("onmouseleave=")
                || lower.contains("ondblclick=")
                || lower.contains("<iframe")
                || lower.contains("<object")
                || lower.contains("<embed")
                || lower.contains("<applet")
                || lower.contains("<form")
                || lower.contains("<input")
                || lower.contains("<textarea")
                || lower.contains("<button")
                || lower.contains("<link")
                || lower.contains("<meta")
                || lower.contains("<style")
                || lower.contains("expression(")
                || lower.contains("eval(")
                || lower.contains("alert(")
                || lower.contains("confirm(")
                || lower.contains("prompt(")
                || lower.contains("document.cookie")
                || lower.contains("document.write")
                || lower.contains("window.location")
                || lower.contains("window.open");
    }
}
