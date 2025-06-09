package com.freesia.notice.repository;


import com.freesia.notice.po.SysNoticePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 消息公告表 持久层
 * @date 2025-06-06
 */
@Repository
public interface SysNoticeRepository extends JpaRepository<SysNoticePo, Long> {
}
