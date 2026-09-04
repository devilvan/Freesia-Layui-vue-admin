package com.freesia.todayhistory.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.exception.ServiceException;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.todayhistory.dto.TodayHistoryItemDto;
import com.freesia.todayhistory.dto.TodayHistoryLinkDto;
import com.freesia.todayhistory.dto.TodayHistoryPageDto;
import com.freesia.todayhistory.dto.TodayHistoryQueryVo;
import com.freesia.todayhistory.parser.TodayHistoryHtmlParser;
import com.freesia.todayhistory.parser.TodayHistoryHtmlParser.ParsedTodayHistoryItem;
import com.freesia.todayhistory.parser.TodayHistoryHtmlParser.ParsedTodayHistoryLink;
import com.freesia.todayhistory.parser.TodayHistoryHtmlParser.ParsedTodayHistoryPage;
import com.freesia.todayhistory.po.TodayHistoryItemPo;
import com.freesia.todayhistory.po.TodayHistoryLinkPo;
import com.freesia.todayhistory.po.TodayHistoryPagePo;
import com.freesia.todayhistory.repository.TodayHistoryItemRepository;
import com.freesia.todayhistory.repository.TodayHistoryLinkRepository;
import com.freesia.todayhistory.repository.TodayHistoryPageRepository;
import com.freesia.todayhistory.service.TodayHistoryService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.*;

/**
 * 历史上的今天业务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TodayHistoryServiceImpl implements TodayHistoryService {
    private static final String WIKIPEDIA_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0 Safari/537.36";
    private final TodayHistoryPageRepository todayHistoryPageRepository;
    private final TodayHistoryItemRepository todayHistoryItemRepository;
    private final TodayHistoryLinkRepository todayHistoryLinkRepository;
    private final TodayHistoryHtmlParser todayHistoryHtmlParser;

    @Override
    public TableResult<TodayHistoryPageDto> findPage(TodayHistoryQueryVo queryVo, PageQuery pageQuery) {
        Pageable pageable = buildPageable(pageQuery);
        org.springframework.data.domain.Page<TodayHistoryPagePo> poPage = todayHistoryPageRepository.findAll(buildSpecification(queryVo), pageable);
        List<TodayHistoryPageDto> dtoList = poPage.getContent().stream().map(this::convertPageSummary).toList();
        Page<TodayHistoryPageDto> resultPage = new Page<>(poPage.getNumber() + 1L, poPage.getSize(), poPage.getTotalElements());
        resultPage.setRecords(dtoList);
        return TableResult.build(resultPage);
    }

    @Override
    public TodayHistoryPageDto findDetail(String historyKey) {
        TodayHistoryPagePo pagePo = todayHistoryPageRepository.findByHistoryKey(normalizeHistoryKey(historyKey))
                .orElseThrow(() -> new ServiceException("历史日期不存在: " + historyKey));
        return buildDetail(pagePo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodayHistoryPageDto syncDay(int monthValue, int dayValue, boolean forceRefresh) {
        validateMonthDay(monthValue, dayValue);
        String historyKey = TodayHistoryHtmlParser.buildHistoryKey(monthValue, dayValue);
        String pageTitle = TodayHistoryHtmlParser.buildPageTitle(monthValue, dayValue);
        String pageUrl = TodayHistoryHtmlParser.buildPageUrl(pageTitle);
        String sourceHtml = fetchPageHtml(pageUrl);
        ParsedTodayHistoryPage parsedPage = todayHistoryHtmlParser.parse(monthValue, dayValue, sourceHtml);

        Optional<TodayHistoryPagePo> existingPageOpt = todayHistoryPageRepository.findByHistoryKey(historyKey);
        if (!forceRefresh && existingPageOpt.isPresent()) {
            TodayHistoryPagePo existing = existingPageOpt.get();
            if (StrUtil.equals(existing.getContentHash(), parsedPage.getContentHash())) {
                return buildDetail(existing);
            }
        }

        TodayHistoryPagePo pagePo = existingPageOpt.orElseGet(TodayHistoryPagePo::new);
        pagePo.setMonthValue(monthValue);
        pagePo.setDayValue(dayValue);
        pagePo.setHistoryKey(historyKey);
        pagePo.setPageTitle(pageTitle);
        pagePo.setPageUrl(pageUrl);
        pagePo.setContentHash(parsedPage.getContentHash());
        pagePo.setLastSyncTime(new java.util.Date());
        pagePo.setRawHtml(parsedPage.getRawHtml());
        pagePo.setItemCount(parsedPage.getItems().size());
        pagePo = todayHistoryPageRepository.saveAndFlush(pagePo);

        todayHistoryItemRepository.deleteByPageId(pagePo.getId());
        todayHistoryLinkRepository.deleteByPageId(pagePo.getId());

        List<TodayHistoryItemPo> itemPoList = new ArrayList<>();
        for (ParsedTodayHistoryItem parsedItem : parsedPage.getItems()) {
            TodayHistoryItemPo itemPo = new TodayHistoryItemPo();
            itemPo.setPageId(pagePo.getId());
            itemPo.setHistoryKey(historyKey);
            itemPo.setItemType(parsedItem.getItemType());
            itemPo.setEraType(parsedItem.getEraType());
            itemPo.setSectionTitle(parsedItem.getSectionTitle());
            itemPo.setEventYear(parsedItem.getEventYear());
            itemPo.setSortNo(parsedItem.getSortNo());
            itemPo.setItemHash(buildItemHash(pagePo.getId(), parsedItem));
            itemPo.setContent(parsedItem.getContent());
            itemPoList.add(itemPo);
        }
        itemPoList = todayHistoryItemRepository.saveAllAndFlush(itemPoList);

        List<TodayHistoryLinkPo> linkPoList = new ArrayList<>();
        for (int i = 0; i < parsedPage.getItems().size(); i++) {
            TodayHistoryItemPo savedItem = itemPoList.get(i);
            ParsedTodayHistoryItem parsedItem = parsedPage.getItems().get(i);
            for (ParsedTodayHistoryLink parsedLink : parsedItem.getLinks()) {
                TodayHistoryLinkPo linkPo = new TodayHistoryLinkPo();
                linkPo.setPageId(pagePo.getId());
                linkPo.setItemId(savedItem.getId());
                linkPo.setHistoryKey(historyKey);
                linkPo.setLinkText(parsedLink.getLinkText());
                linkPo.setLinkUrl(parsedLink.getLinkUrl());
                linkPo.setLinkTitle(parsedLink.getLinkTitle());
                linkPo.setInternalFlag(parsedLink.getInternalFlag());
                linkPo.setSortNo(parsedLink.getSortNo());
                linkPo.setLinkHash(buildLinkHash(pagePo.getId(), savedItem.getId(), parsedLink));
                linkPoList.add(linkPo);
            }
        }
        if (CollUtil.isNotEmpty(linkPoList)) {
            todayHistoryLinkRepository.saveAllAndFlush(linkPoList);
        }
        return buildDetail(pagePo);
    }

    @Override
    public List<TodayHistoryPageDto> syncAll(boolean forceRefresh) {
        List<TodayHistoryPageDto> result = new ArrayList<>();
        for (int monthValue = 1; monthValue <= 12; monthValue++) {
            int maxDay = YearMonth.of(2024, monthValue).lengthOfMonth();
            for (int dayValue = 1; dayValue <= maxDay; dayValue++) {
                try {
                    TodayHistoryService proxy = (TodayHistoryService) AopContext.currentProxy();
                    result.add(proxy.syncDay(monthValue, dayValue, forceRefresh));
                } catch (Exception e) {
                    log.warn("同步历史上的今天失败: {}-{}, {}", monthValue, dayValue, e.getMessage(), e);
                }
            }
        }
        return result;
    }

    private Specification<TodayHistoryPagePo> buildSpecification(TodayHistoryQueryVo queryVo) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (queryVo == null) {
                query.orderBy(builder.asc(root.get("monthValue")), builder.asc(root.get("dayValue")));
                return builder.and(predicates.toArray(new Predicate[0]));
            }
            if (queryVo.getMonthValue() != null) {
                predicates.add(builder.equal(root.get("monthValue"), queryVo.getMonthValue()));
            }
            if (queryVo.getDayValue() != null) {
                predicates.add(builder.equal(root.get("dayValue"), queryVo.getDayValue()));
            }
            if (StrUtil.isNotBlank(queryVo.getHistoryKey())) {
                predicates.add(builder.equal(root.get("historyKey"), normalizeHistoryKey(queryVo.getHistoryKey())));
            }
            if (StrUtil.isNotBlank(queryVo.getPageTitle())) {
                predicates.add(builder.like(root.get("pageTitle"), "%" + queryVo.getPageTitle().trim() + "%"));
            }
            query.orderBy(builder.asc(root.get("monthValue")), builder.asc(root.get("dayValue")));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Pageable buildPageable(PageQuery pageQuery) {
        if (pageQuery == null) {
            return PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "monthValue", "dayValue"));
        }
        int pageNum = Optional.ofNullable(pageQuery.getCurrent()).orElse(PageQuery.DEFAULT_PAGE_NUM);
        int pageSize = Optional.ofNullable(pageQuery.getLimit()).orElse(PageQuery.DEFAULT_PAGE_SIZE);
        if (pageNum <= 0) {
            pageNum = PageQuery.DEFAULT_PAGE_NUM;
        }
        return PageRequest.of(pageNum - 1, pageSize, buildSort(pageQuery));
    }

    private Sort buildSort(PageQuery pageQuery) {
        if (StrUtil.isBlank(pageQuery.getOrderByColumn()) || StrUtil.isBlank(pageQuery.getIsAsc())) {
            return Sort.by(Sort.Direction.ASC, "monthValue", "dayValue");
        }
        String[] columns = pageQuery.getOrderByColumn().split(",");
        String[] directions = pageQuery.getIsAsc().split(",");
        if (directions.length != 1 && directions.length != columns.length) {
            throw new ServiceException("排序参数有误");
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i < columns.length; i++) {
            String column = columns[i].trim();
            String direction = directions.length == 1 ? directions[0].trim() : directions[i].trim();
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            orders.add(new Sort.Order(sortDirection, column));
        }
        return Sort.by(orders);
    }

    private TodayHistoryPageDto convertPageSummary(TodayHistoryPagePo po) {
        TodayHistoryPageDto dto = new TodayHistoryPageDto();
        dto.setId(po.getId());
        dto.setMonthValue(po.getMonthValue());
        dto.setDayValue(po.getDayValue());
        dto.setHistoryKey(po.getHistoryKey());
        dto.setPageTitle(po.getPageTitle());
        dto.setPageUrl(po.getPageUrl());
        dto.setContentHash(po.getContentHash());
        dto.setLastSyncTime(po.getLastSyncTime());
        dto.setItemCount(po.getItemCount());
        return dto;
    }

    private TodayHistoryPageDto buildDetail(TodayHistoryPagePo pagePo) {
        TodayHistoryPageDto dto = convertPageSummary(pagePo);
        List<TodayHistoryItemPo> itemPoList = todayHistoryItemRepository.findByPageIdOrderBySortNoAsc(pagePo.getId());
        List<TodayHistoryLinkPo> linkPoList = todayHistoryLinkRepository.findByPageIdOrderBySortNoAsc(pagePo.getId());
        Map<Long, List<TodayHistoryLinkDto>> linkMap = new LinkedHashMap<>();
        for (TodayHistoryLinkPo linkPo : linkPoList) {
            TodayHistoryLinkDto linkDto = convertLink(linkPo);
            linkMap.computeIfAbsent(linkPo.getItemId(), key -> new ArrayList<>()).add(linkDto);
        }
        List<TodayHistoryItemDto> itemDtos = new ArrayList<>();
        for (TodayHistoryItemPo itemPo : itemPoList) {
            TodayHistoryItemDto itemDto = convertItem(itemPo);
            itemDto.setLinks(linkMap.getOrDefault(itemPo.getId(), List.of()));
            itemDtos.add(itemDto);
        }
        dto.setItems(itemDtos);
        return dto;
    }

    private TodayHistoryItemDto convertItem(TodayHistoryItemPo po) {
        TodayHistoryItemDto dto = new TodayHistoryItemDto();
        dto.setId(po.getId());
        dto.setPageId(po.getPageId());
        dto.setHistoryKey(po.getHistoryKey());
        dto.setItemType(po.getItemType());
        dto.setEraType(po.getEraType());
        dto.setSectionTitle(po.getSectionTitle());
        dto.setEventYear(po.getEventYear());
        dto.setSortNo(po.getSortNo());
        dto.setContent(po.getContent());
        return dto;
    }

    private TodayHistoryLinkDto convertLink(TodayHistoryLinkPo po) {
        TodayHistoryLinkDto dto = new TodayHistoryLinkDto();
        dto.setId(po.getId());
        dto.setPageId(po.getPageId());
        dto.setItemId(po.getItemId());
        dto.setHistoryKey(po.getHistoryKey());
        dto.setLinkText(po.getLinkText());
        dto.setLinkUrl(po.getLinkUrl());
        dto.setLinkTitle(po.getLinkTitle());
        dto.setInternalFlag(po.getInternalFlag());
        dto.setSortNo(po.getSortNo());
        return dto;
    }

    private String fetchPageHtml(String pageUrl) {
        try (HttpResponse response = HttpRequest.post(pageUrl)
                .header("User-Agent", WIKIPEDIA_USER_AGENT)
                .header("Accept-Language", "zh-CN, zh;q=0.9")
                .setHttpProxy("127.0.0.1", 7897)
                .timeout(20000)
                .execute()) {
            if (!response.isOk()) {
                throw new ServiceException("抓取历史页面失败: " + response.getStatus());
            }
            return response.body();
        } catch (Exception e) {
            throw new ServiceException("抓取历史页面失败: " + e.getMessage());
        }
    }

    private void validateMonthDay(int monthValue, int dayValue) {
        if (monthValue < 1 || monthValue > 12) {
            throw new ServiceException("月份必须在 1-12 之间");
        }
        int maxDay = YearMonth.of(2024, monthValue).lengthOfMonth();
        if (dayValue < 1 || dayValue > maxDay) {
            throw new ServiceException("日期不合法: " + monthValue + "-" + dayValue);
        }
    }

    private String normalizeHistoryKey(String historyKey) {
        if (StrUtil.isBlank(historyKey)) {
            return historyKey;
        }
        String normalized = historyKey.trim();
        if (normalized.matches("^\\d{1,2}-\\d{1,2}$")) {
            String[] parts = normalized.split("-");
            return String.format("%02d-%02d", Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
        if (normalized.matches("^\\d{1,2}月\\d{1,2}日$")) {
            int monthValue = Integer.parseInt(normalized.substring(0, normalized.indexOf('月')));
            int dayValue = Integer.parseInt(normalized.substring(normalized.indexOf('月') + 1, normalized.indexOf('日')));
            return TodayHistoryHtmlParser.buildHistoryKey(monthValue, dayValue);
        }
        return normalized;
    }

    private String buildItemHash(Long pageId, ParsedTodayHistoryItem parsedItem) {
        return DigestUtil.sha256Hex(String.join("|",
                String.valueOf(pageId),
                StrUtil.nullToDefault(parsedItem.getItemType(), ""),
                StrUtil.nullToDefault(parsedItem.getEraType(), ""),
                StrUtil.nullToDefault(parsedItem.getSectionTitle(), ""),
                parsedItem.getEventYear() == null ? "" : String.valueOf(parsedItem.getEventYear()),
                StrUtil.nullToDefault(parsedItem.getContent(), "")));
    }

    private String buildLinkHash(Long pageId, Long itemId, ParsedTodayHistoryLink parsedLink) {
        return DigestUtil.sha256Hex(String.join("|",
                String.valueOf(pageId),
                String.valueOf(itemId),
                StrUtil.nullToDefault(parsedLink.getLinkText(), ""),
                StrUtil.nullToDefault(parsedLink.getLinkUrl(), ""),
                StrUtil.nullToDefault(parsedLink.getLinkTitle(), ""),
                parsedLink.getInternalFlag() == null ? "" : String.valueOf(parsedLink.getInternalFlag())));
    }
}
