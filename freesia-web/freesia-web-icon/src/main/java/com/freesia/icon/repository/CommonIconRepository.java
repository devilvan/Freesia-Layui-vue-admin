package com.freesia.icon.repository;


import com.freesia.icon.po.CommonIconPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标表 持久层
 * @date 2025-03-26
 */
@Repository
public interface CommonIconRepository extends JpaRepository<CommonIconPo, Long> {
}
