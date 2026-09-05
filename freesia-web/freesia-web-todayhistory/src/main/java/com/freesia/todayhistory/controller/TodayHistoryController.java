package com.freesia.todayhistory.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.freesia.controller.BaseController;
import com.freesia.pojo.PageQuery;
import com.freesia.exception.ServiceException;
import com.freesia.pojo.TableResult;
import com.freesia.todayhistory.dto.TodayHistoryPageDto;
import com.freesia.todayhistory.dto.TodayHistoryQueryVo;
import com.freesia.todayhistory.dto.TodayHistorySearchResultDto;
import com.freesia.todayhistory.service.TodayHistoryService;
import com.freesia.todayhistory.vo.TodayHistorySyncVo;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 历史上的今天控制器。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todayHistoryController")
@Tag(name = "TodayHistoryController", description = "历史上的今天控制器")
public class TodayHistoryController extends BaseController {
    private final TodayHistoryService todayHistoryService;

    @Operation(summary = "分页查询历史上的今天页面")
    @GetMapping("findPageTodayHistory")
    public TableResult<TodayHistoryPageDto> findPageTodayHistory(TodayHistoryQueryVo queryVo, PageQuery pageQuery) {
        return todayHistoryService.findPage(queryVo, pageQuery);
    }

    @SaIgnore
    @Operation(summary = "查询历史上的今天详情")
    @GetMapping("findTodayHistoryDetail/{historyKey}")
    public R<TodayHistoryPageDto> findTodayHistoryDetail(@PathVariable String historyKey) {
        return R.ok(todayHistoryService.findDetail(historyKey));
    }

    @Operation(summary = "全局关键词查询历史上的今天词条")
    @GetMapping("searchTodayHistory")
    public R<List<TodayHistorySearchResultDto>> searchTodayHistory(@RequestParam String keyword) {
        return R.ok(todayHistoryService.searchGlobal(keyword));
    }

    @Operation(summary = "手动同步指定日期")
    @PostMapping("syncTodayHistory")
    public R<TodayHistoryPageDto> syncTodayHistory(@RequestBody TodayHistorySyncVo syncVo) {
        String historyKey = syncVo.getHistoryKey();
        int monthValue = syncVo.getMonthValue() == null ? 0 : syncVo.getMonthValue();
        int dayValue = syncVo.getDayValue() == null ? 0 : syncVo.getDayValue();
        if ((monthValue <= 0 || dayValue <= 0) && historyKey != null && historyKey.contains("-")) {
            String[] parts = historyKey.split("-");
            monthValue = Integer.parseInt(parts[0]);
            dayValue = Integer.parseInt(parts[1]);
        }
        if (monthValue <= 0 || dayValue <= 0) {
            throw new ServiceException("请传入 monthValue/dayValue 或 historyKey");
        }
        boolean forceRefresh = Boolean.TRUE.equals(syncVo.getForceRefresh());
        return R.ok(todayHistoryService.syncDay(monthValue, dayValue, forceRefresh));
    }

    @Operation(summary = "同步全部历史上的今天页面")
    @PostMapping("syncAllTodayHistory")
    public R<List<TodayHistoryPageDto>> syncAllTodayHistory(@RequestBody(required = false) TodayHistorySyncVo syncVo) {
        boolean forceRefresh = syncVo != null && Boolean.TRUE.equals(syncVo.getForceRefresh());
        return R.ok(todayHistoryService.syncAll(forceRefresh));
    }
}
