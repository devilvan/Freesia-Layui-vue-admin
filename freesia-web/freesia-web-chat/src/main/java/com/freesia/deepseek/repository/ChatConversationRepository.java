package com.freesia.deepseek.repository;


import com.freesia.deepseek.po.ChatConversationPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Evad.Wu
 * @Description 交互式会话 持久层
 * @date 2026-07-19
 */
@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversationPo, Long> {

    Optional<ChatConversationPo> findByExtId(String extId);

    List<ChatConversationPo> findByUserIdOrderByCreateTimeDesc(Long userId);
}
