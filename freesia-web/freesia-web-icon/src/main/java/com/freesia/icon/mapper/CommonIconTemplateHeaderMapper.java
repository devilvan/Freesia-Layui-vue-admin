package com.freesia.icon.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.icon.po.CommonIconTemplateHeaderPo;
import com.freesia.pojo.LaySelect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 持久层
 * @date 2025-04-07
 */
@Mapper
public interface CommonIconTemplateHeaderMapper extends BaseMapper<CommonIconTemplateHeaderPo> {
    /**
     * 查询最大排序号
     *
     * @return 最大排序号
     */
    Integer findMaxOrderNum();

    /**
     * 根据用户ID查询图标模板头表是否已经存在默认的数据
     *
     * @param userId 用户ID
     * @return 是否已经存在默认的数据
     */
    boolean findExistsDefaultFlag(@Param(value = "userId") Long userId);

    /**
     * 根据用户ID查询开销类型下拉集合
     *
     * @param userId 用户ID
     * @return 开销类型下拉集合
     */
    List<LaySelect> findListSelectCostType(@Param(value = "userId") Long userId);
}
