package com.freesia.icon.repository;


import com.freesia.icon.po.CommonIconTemplateHeaderPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    /**
     * 根据用户ID查询图标模板头
     *
     * @param userId 用户ID
     * @return 图标模板头
     */
    Optional<CommonIconTemplateHeaderPo> findByUserId(Long userId);
}
