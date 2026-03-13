package com.freesia.repository;


import com.freesia.dto.MarkReadDto;
import com.freesia.po.SysNoticePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Evad.Wu
 * @Description 消息公告表 持久层
 * @date 2025-06-06
 */
@Repository
public interface SysNoticeRepository extends JpaRepository<SysNoticePo, Long> {
    /**
     * 标记已读
     *
     * @param markReadDto 更新条件
     */
    @Modifying
    @Query(value = """
            UPDATE SysNoticePo
                SET readFlag = 1
            WHERE id IN (:#{#markReadDto.idList})
            """)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    void markRead(@Param("markReadDto") MarkReadDto markReadDto);
}
