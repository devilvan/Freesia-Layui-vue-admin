package com.freesia.repository;


import com.freesia.po.SysOssPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description OSS对象存储表 持久层
 * @date 2024-02-27
 */
@Repository
public interface SysOssRepository extends JpaRepository<SysOssPo, Long> {
}
