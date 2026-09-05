package com.freesia.todayhistory.repository;

import com.freesia.todayhistory.po.TodayHistoryItemPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 历史上的今天-条目仓储.
 */
@Repository
public interface TodayHistoryItemRepository extends JpaRepository<TodayHistoryItemPo, Long> {
}
