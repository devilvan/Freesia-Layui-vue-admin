package com.freesia.icon.repository;


import com.freesia.icon.po.CommonIconTemplateDetailPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板表 持久层
 * @date 2025-04-07
 */
@Repository
public interface CommonIconTemplateDetailRepository extends JpaRepository<CommonIconTemplateDetailPo, Long> {
    /**
     * 删除自定义分组
     *
     * @param parentId 图标ID
     */
    @Modifying
    @Query(value = """
            DELETE FROM CommonIconTemplateDetailPo detail
            where 1=1
            and detail.logicDel = false
            and (detail.id = :parentId or detail.parentId = :parentId)
            """)
    @Transactional(rollbackFor = Exception.class)
    void deleteGrouping(@Param("parentId") Long parentId);

    /**
     * 查询所有内置图标
     *
     * @return 结果集
     */
    List<CommonIconTemplateDetailPo> findAllByBuildInTrue();

    Boolean findByHeaderIdExists(Long headerId);
}
