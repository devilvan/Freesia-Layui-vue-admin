package com.freesia.icon.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.icon.po.CommonIconTemplateHeaderPo;
import org.apache.ibatis.annotations.Mapper;

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
}
