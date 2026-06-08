package com.freesia.icon.repository;


import com.freesia.icon.po.CommonIconTemplateHeaderPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 持久层
 * @date 2025-04-07
 */
@Repository
public interface CommonIconTemplateHeaderRepository extends JpaRepository<CommonIconTemplateHeaderPo, Long> {
    /**
     * 查询默认通用图标模板头表
     *
     * @return 默认通用图标模板头表
     */
    CommonIconTemplateHeaderPo findFirstByBuildInTrueOrderByCreateTime();
}
