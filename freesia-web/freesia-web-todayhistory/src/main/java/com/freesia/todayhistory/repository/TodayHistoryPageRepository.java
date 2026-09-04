package com.freesia.todayhistory.repository;

import com.freesia.todayhistory.po.TodayHistoryPagePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 历史上的今天-页面仓储。
 */
@Repository
public interface TodayHistoryPageRepository extends JpaRepository<TodayHistoryPagePo, Long>, JpaSpecificationExecutor<TodayHistoryPagePo> {
    Optional<TodayHistoryPagePo> findByHistoryKey(String historyKey);
}
