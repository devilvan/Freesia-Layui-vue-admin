package com.freesia.icon.repository;


import com.freesia.icon.po.CommonIconTemplateHeaderPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 持久层
 * @date 2025-04-07
 */
@Repository
public interface CommonIconTemplateHeaderRepository extends JpaRepository<CommonIconTemplateHeaderPo, Long> {
}
