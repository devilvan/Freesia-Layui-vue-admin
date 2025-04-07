package com.freesia.icon.repository;


import com.freesia.icon.po.CommonIconTemplateDetailPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板表 持久层
 * @date 2025-04-07
 */
@Repository
public interface CommonIconTemplateDetailRepository extends JpaRepository<CommonIconTemplateDetailPo, Long> {
}
