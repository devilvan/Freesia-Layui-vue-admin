package com.freesia.repository;


import com.freesia.po.SleepCommentHeaderPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 睡眠产品评论 持久层
 * @date 2026-03-23
 */
@Repository
public interface SleepCommentHeaderRepository extends JpaRepository<SleepCommentHeaderPo, Long> {
}
