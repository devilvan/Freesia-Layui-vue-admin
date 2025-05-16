package com.freesia.icon.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.icon.dto.CommonIconTemplateDetailDto;
import com.freesia.icon.entity.FindCommonIconTemplateDetailEntity;
import com.freesia.icon.entity.FindTreeIconTreeTypeEntity;
import com.freesia.icon.po.CommonIconTemplateDetailPo;
import com.freesia.pojo.LayMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板表 持久层
 * @date 2025-04-07
 */
@Mapper
public interface CommonIconTemplateDetailMapper extends BaseMapper<CommonIconTemplateDetailPo> {

    /**
     * 根据图标ID查询图标模板明细
     *
     * @param dto 入参
     * @return 结果集
     */
    FindCommonIconTemplateDetailEntity findCommonIconTemplateDetail(@Param(value = "dto") CommonIconTemplateDetailDto dto);

    /**
     * 查询通用图标模板明细的节点数据
     *
     * @param dto 查询入参
     * @return 通用图标模板明细的节点数据
     */
    List<FindTreeIconTreeTypeEntity> findTreeIconTreeType(@Param(value = "dto") CommonIconTemplateDetailDto dto);

    /**
     * 查询自增排序号
     *
     * @param dto 查询入参
     * @return 最大排序号
     */
    Integer findMaxOrderNum(@Param(value = "dto") CommonIconTemplateDetailDto dto);

    /**
     * 查询自定义分组列表
     *
     * @param dto 查询入参
     * @return 结果集
     */
    List<LayMenu> findGrouping(@Param(value = "dto") CommonIconTemplateDetailDto dto);

    /**
     * 查询自定义分组Map
     *
     * @param dto 查询入参
     * @return 结果集
     */
    List<FindTreeIconTreeTypeEntity> findCustomIconTemplateDetail(@Param(value = "dto") CommonIconTemplateDetailDto dto);
}
