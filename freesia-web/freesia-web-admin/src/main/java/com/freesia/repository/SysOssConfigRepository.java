package com.freesia.repository;


import com.freesia.po.SysOssConfigPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description OSS配置信息表 持久层
 * @date 2024-02-28
 */
@Repository
public interface SysOssConfigRepository extends JpaRepository<SysOssConfigPo, Long> {
}
