package com.freesia.repository;


import com.freesia.po.SysSensitiveLogPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 敏感操作信息表 持久层
 * @date 2023-08-13
 */
@Repository
public interface SysSensitiveLogRepository extends JpaRepository<SysSensitiveLogPo, Long> {
}
