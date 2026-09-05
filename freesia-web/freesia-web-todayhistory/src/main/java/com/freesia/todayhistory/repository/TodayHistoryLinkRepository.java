package com.freesia.todayhistory.repository;

import com.freesia.todayhistory.po.TodayHistoryLinkPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 历史上的今天-链接仓储。
 */
@Repository
public interface TodayHistoryLinkRepository extends JpaRepository<TodayHistoryLinkPo, Long> {
    List<TodayHistoryLinkPo> findByPageIdOrderBySortNoAsc(Long pageId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from TodayHistoryLinkPo link where link.pageId = :pageId")
    int deleteByPageId(Long pageId);
}
