package com.freesia.todayhistory.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.todayhistory.dto.TodayHistoryPageDto;
import com.freesia.todayhistory.dto.TodayHistoryQueryVo;

import java.util.List;

/**
 * 历史上的今天业务接口。
 */
public interface TodayHistoryService {
    TableResult<TodayHistoryPageDto> findPage(TodayHistoryQueryVo queryVo, PageQuery pageQuery);

    TodayHistoryPageDto findDetail(String historyKey);

    TodayHistoryPageDto syncDay(int monthValue, int dayValue, boolean forceRefresh);

    List<TodayHistoryPageDto> syncAll(boolean forceRefresh);
}

