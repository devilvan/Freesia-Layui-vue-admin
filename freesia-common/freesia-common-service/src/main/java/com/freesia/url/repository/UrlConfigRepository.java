package com.freesia.url.repository;


import com.freesia.url.po.UrlConfigPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description URL配置信息表 持久层
 * @date 2024-01-24
 */
@Repository
public interface UrlConfigRepository extends JpaRepository<UrlConfigPo, Long> {
}
