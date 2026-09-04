package com.freesia.todayhistory.parser;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.freesia.todayhistory.TodayHistoryEraType;
import com.freesia.todayhistory.TodayHistoryItemType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 历史上的今天 HTML 解析器。
 */
@Slf4j
@Component
public class TodayHistoryHtmlParser {
    private static final Pattern YEAR_PATTERN = Pattern.compile("^(?:公元前|前)?(\\d{1,4})年");
    private static final String WIKIPEDIA_HOST = "https://zh.wikipedia.org";

    public ParsedTodayHistoryPage parse(int monthValue, int dayValue, String sourceHtml) {
        String pageTitle = buildPageTitle(monthValue, dayValue);
        String historyKey = buildHistoryKey(monthValue, dayValue);
        String pageUrl = buildPageUrl(pageTitle);
        String rawHtml = normalizeHtml(sourceHtml);
        Document document = Jsoup.parse(rawHtml, pageUrl);
        Element content = document.selectFirst("#mw-content-text .mw-parser-output");
        if (content == null) {
            throw new IllegalArgumentException("无法定位维基百科正文区域");
        }
        ParsedTodayHistoryPage parsedPage = new ParsedTodayHistoryPage();
        parsedPage.setMonthValue(monthValue);
        parsedPage.setDayValue(dayValue);
        parsedPage.setHistoryKey(historyKey);
        parsedPage.setPageTitle(pageTitle);
        parsedPage.setPageUrl(pageUrl);
        parsedPage.setRawHtml(rawHtml);
        parsedPage.setContentHash(DigestUtil.sha256Hex(rawHtml));

        SectionState sectionState = new SectionState();
        int currentSort = 1;
        for (Element child : content.children()) {
            if (isHeading(child)) {
                SectionState nextState = classifyHeading(child.text(), sectionState);
                if (!sectionState.isSameSection(nextState)) {
                    currentSort = 1;
                }
                sectionState = nextState;
                continue;
            }
            String tagName = child.tagName();
            if (isList(tagName) && sectionState.itemType != TodayHistoryItemType.UNKNOWN) {
                Elements listItems = child.children();
                for (Element li : listItems) {
                    if (!"li".equalsIgnoreCase(li.tagName())) {
                        continue;
                    }
                    String contentText = normalizeText(li.text());
                    if (StrUtil.isBlank(contentText)) {
                        continue;
                    }
                    ParsedTodayHistoryItem parsedItem = new ParsedTodayHistoryItem();
                    parsedItem.setItemType(sectionState.itemType.getCode());
                    parsedItem.setEraType(resolveEraType(sectionState, contentText).getCode());
                    parsedItem.setSectionTitle(sectionState.sectionTitle);
                    parsedItem.setEventYear(parseYear(contentText));
                    parsedItem.setSortNo(currentSort++);
                    parsedItem.setContent(contentText);
                    parsedItem.setLinks(parseLinks(li));
                    parsedPage.getItems().add(parsedItem);
                }
            }
        }
        parsedPage.setItems(deduplicateItems(parsedPage.getItems()));
        return parsedPage;
    }

    private SectionState classifyHeading(String heading, SectionState currentState) {
        String normalized = normalizeHeading(heading);
        if (StrUtil.isBlank(normalized)) {
            return currentState.copy();
        }
        TodayHistoryItemType itemType = TodayHistoryItemType.fromHeading(normalized);
        if (itemType != TodayHistoryItemType.UNKNOWN) {
            SectionState state = new SectionState();
            state.itemType = itemType;
            state.eraType = TodayHistoryEraType.NONE;
            state.sectionTitle = itemType.getLabel();
            return state;
        }
        if (currentState.itemType == TodayHistoryItemType.EVENT) {
            TodayHistoryEraType eraType = TodayHistoryEraType.fromHeading(normalized);
            if (eraType != TodayHistoryEraType.NONE) {
                SectionState state = currentState.copy();
                state.eraType = eraType;
                return state;
            }
        }
        SectionState state = new SectionState();
        state.itemType = TodayHistoryItemType.UNKNOWN;
        state.eraType = TodayHistoryEraType.NONE;
        state.sectionTitle = normalized;
        return state;
    }

    private TodayHistoryEraType resolveEraType(SectionState state, String contentText) {
        if (state.itemType == TodayHistoryItemType.EVENT) {
            if (state.eraType != TodayHistoryEraType.NONE) {
                return state.eraType;
            }
            Integer year = parseYear(contentText);
            return TodayHistoryEraType.fromYear(year);
        }
        return TodayHistoryEraType.NONE;
    }

    private Integer parseYear(String contentText) {
        if (StrUtil.isBlank(contentText)) {
            return null;
        }
        Matcher matcher = YEAR_PATTERN.matcher(contentText);
        if (!matcher.find()) {
            return null;
        }
        Integer year = Integer.parseInt(matcher.group(1));
        if (contentText.startsWith("前") || contentText.startsWith("公元前")) {
            return -year;
        }
        return year;
    }

    private List<ParsedTodayHistoryLink> parseLinks(Element li) {
        List<ParsedTodayHistoryLink> links = new ArrayList<>();
        Map<String, ParsedTodayHistoryLink> linkMap = new LinkedHashMap<>();
        int sortNo = 1;
        for (Element anchor : li.select("a[href]")) {
            String href = anchor.attr("href");
            if (!isContentLink(href)) {
                continue;
            }
            String absoluteUrl = anchor.absUrl("href");
            if (StrUtil.isBlank(absoluteUrl)) {
                if (href.startsWith("/wiki/")) {
                    absoluteUrl = WIKIPEDIA_HOST + href;
                } else {
                    absoluteUrl = href;
                }
            }
            ParsedTodayHistoryLink link = new ParsedTodayHistoryLink();
            link.setLinkText(normalizeText(anchor.text()));
            link.setLinkUrl(absoluteUrl);
            link.setLinkTitle(normalizeText(anchor.attr("title")));
            link.setInternalFlag(href.startsWith("/wiki/"));
            link.setSortNo(sortNo++);
            String key = buildLinkKey(link);
            linkMap.putIfAbsent(key, link);
        }
        links.addAll(linkMap.values());
        return links;
    }

    private List<ParsedTodayHistoryItem> deduplicateItems(List<ParsedTodayHistoryItem> items) {
        Map<String, ParsedTodayHistoryItem> itemMap = new LinkedHashMap<>();
        for (ParsedTodayHistoryItem item : items) {
            String key = buildItemKey(item);
            itemMap.putIfAbsent(key, item);
        }
        List<ParsedTodayHistoryItem> result = new ArrayList<>(itemMap.values());
        for (ParsedTodayHistoryItem item : result) {
            item.setLinks(deduplicateLinks(item.getLinks()));
        }
        return result;
    }

    private List<ParsedTodayHistoryLink> deduplicateLinks(List<ParsedTodayHistoryLink> links) {
        Map<String, ParsedTodayHistoryLink> linkMap = new LinkedHashMap<>();
        for (ParsedTodayHistoryLink link : links) {
            linkMap.putIfAbsent(buildLinkKey(link), link);
        }
        return new ArrayList<>(linkMap.values());
    }

    private String buildItemKey(ParsedTodayHistoryItem item) {
        return String.join("|",
                StrUtil.nullToDefault(item.getItemType(), ""),
                StrUtil.nullToDefault(item.getEraType(), ""),
                StrUtil.nullToDefault(item.getSectionTitle(), ""),
                item.getEventYear() == null ? "" : String.valueOf(item.getEventYear()),
                StrUtil.nullToDefault(item.getContent(), ""));
    }

    private String buildLinkKey(ParsedTodayHistoryLink link) {
        return String.join("|",
                StrUtil.nullToDefault(link.getLinkUrl(), ""),
                StrUtil.nullToDefault(link.getLinkText(), ""),
                StrUtil.nullToDefault(link.getLinkTitle(), ""),
                link.getInternalFlag() == null ? "" : String.valueOf(link.getInternalFlag()));
    }

    private boolean isContentLink(String href) {
        if (StrUtil.isBlank(href)) {
            return false;
        }
        String normalized = href.trim();
        if (normalized.startsWith("#")) {
            return false;
        }
        if (normalized.contains("cite_note") || normalized.contains("cite_ref")) {
            return false;
        }
        return normalized.startsWith("/wiki/") || normalized.startsWith("http://") || normalized.startsWith("https://");
    }

    private String normalizeHtml(String sourceHtml) {
        if (StrUtil.isBlank(sourceHtml)) {
            return "";
        }
        Document sourceDocument = Jsoup.parse(sourceHtml);
        Elements sourceLines = sourceDocument.select("td.line-content");
        if (sourceLines.isEmpty()) {
            return sourceHtml;
        }
        StringBuilder builder = new StringBuilder();
        for (Element line : sourceLines) {
            String lineText = line.wholeText();
            if (lineText == null) {
                continue;
            }
            builder.append(lineText.stripTrailing()).append('\n');
        }
        String normalizedHtml = builder.toString().trim();
        return StrUtil.isBlank(normalizedHtml) ? sourceHtml : normalizedHtml;
    }

    private String normalizeText(String text) {
        if (text == null) {
            return null;
        }
        return text.replace('\u00A0', ' ')
                .replaceAll("[\\t\\n\\r]+", " ")
                .replaceAll(" {2,}", " ")
                .trim();
    }

    private String normalizeHeading(String heading) {
        String normalized = normalizeText(heading);
        if (normalized == null) {
            return null;
        }
        return normalized.replace("[编辑]", "").trim();
    }

    private boolean isHeading(Element element) {
        String tagName = element.tagName();
        if ("h2".equalsIgnoreCase(tagName) || "h3".equalsIgnoreCase(tagName) || "h4".equalsIgnoreCase(tagName)) {
            return true;
        }
        if ("div".equalsIgnoreCase(tagName)) {
            String text = normalizeText(element.text());
            return StrUtil.isNotBlank(text) && text.endsWith("[编辑]");
        }
        return false;
    }

    private boolean isList(String tagName) {
        return "ul".equalsIgnoreCase(tagName) || "ol".equalsIgnoreCase(tagName);
    }

    public static String buildHistoryKey(int monthValue, int dayValue) {
        return String.format("%02d-%02d", monthValue, dayValue);
    }

    public static String buildPageTitle(int monthValue, int dayValue) {
        return monthValue + "月" + dayValue + "日";
    }

    public static String buildPageUrl(String pageTitle) {
        return WIKIPEDIA_HOST + "/wiki/" + URLEncoder.encode(pageTitle, StandardCharsets.UTF_8);
    }

    @Data
    public static class ParsedTodayHistoryPage {
        private Integer monthValue;
        private Integer dayValue;
        private String historyKey;
        private String pageTitle;
        private String pageUrl;
        private String contentHash;
        private String rawHtml;
        private List<ParsedTodayHistoryItem> items = new ArrayList<>();
    }

    @Data
    public static class ParsedTodayHistoryItem {
        private String itemType;
        private String eraType;
        private String sectionTitle;
        private Integer eventYear;
        private Integer sortNo;
        private String content;
        private List<ParsedTodayHistoryLink> links = new ArrayList<>();
    }

    @Data
    public static class ParsedTodayHistoryLink {
        private String linkText;
        private String linkUrl;
        private String linkTitle;
        private Boolean internalFlag;
        private Integer sortNo;
    }

    @Data
    private static class SectionState {
        private TodayHistoryItemType itemType = TodayHistoryItemType.UNKNOWN;
        private TodayHistoryEraType eraType = TodayHistoryEraType.NONE;
        private String sectionTitle = "";

        private SectionState copy() {
            SectionState copy = new SectionState();
            copy.itemType = this.itemType;
            copy.eraType = this.eraType;
            copy.sectionTitle = this.sectionTitle;
            return copy;
        }

        private boolean isSameSection(SectionState other) {
            if (other == null) {
                return false;
            }
            return itemType == other.itemType && eraType == other.eraType && StrUtil.equals(sectionTitle, other.sectionTitle);
        }
    }
}
