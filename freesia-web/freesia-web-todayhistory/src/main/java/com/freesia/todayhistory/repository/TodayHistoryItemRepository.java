package com.freesia.todayhistory.repository;

import com.freesia.todayhistory.po.TodayHistoryItemPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 历史上的今天-条目仓储。
 */
@Repository
public interface TodayHistoryItemRepository extends JpaRepository<TodayHistoryItemPo, Long> {
    List<TodayHistoryItemPo> findByPageIdOrderBySortNoAsc(Long pageId);

    void deleteByPageId(Long pageId);
}
