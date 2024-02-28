package com.freesia.oss.repository;


import com.freesia.oss.po.SysOssConfigPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description OSS配置信息表 持久层
 * @date 2024-02-28
 */
@Repository
public interface SysOssConfigRepository extends JpaRepository<SysOssConfigPo, Long> {
}
