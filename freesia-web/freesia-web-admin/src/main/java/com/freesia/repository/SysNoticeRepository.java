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
     *
     * Hibernate 6 / Spring Boot 3 说明:
     *   严格模式下 JPQL 解析器不识别 Spring SpEL 参数语法 :#{...} 。
     *   本方法是简单批量 UPDATE, 改用 native SQL 绕过 JPQL 验证, 方法签名/调用点均保持不变。
     */
    @Modifying
    @Query(value = """
            UPDATE SYS_NOTICE
               SET READ_FLAG = 1
             WHERE ID IN (:#{#markReadDto.idList})
            """, nativeQuery = true)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    void markRead(@Param("markReadDto") MarkReadDto markReadDto);
}
