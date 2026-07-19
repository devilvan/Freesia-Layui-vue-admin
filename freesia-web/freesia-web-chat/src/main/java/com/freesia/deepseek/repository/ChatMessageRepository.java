package com.freesia.deepseek.repository;


import com.freesia.deepseek.po.ChatMessagePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 交互式会话-消息 持久层
 * @date 2026-07-19
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessagePo, Long> {
}
