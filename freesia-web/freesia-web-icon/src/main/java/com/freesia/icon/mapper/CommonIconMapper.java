package com.freesia.icon.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.icon.dto.CommonIconDto;
import com.freesia.icon.entity.FindCommonIconEntity;
import com.freesia.icon.entity.FindPageCommonIconEntity;
import com.freesia.icon.po.CommonIconPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Evad.Wu
 * @Description 通用图标表 持久层
 * @date 2025-03-26
 */
@Mapper
public interface CommonIconMapper extends BaseMapper<CommonIconPo> {
    /**
     * 查询通用图标表信息
     *
     * @param commonIconDto 查询条件
     * @param page          分页条件
     * @return 分页信息
     */
    Page<FindPageCommonIconEntity> findPageCommonIcon(@Param(value = "commonIconDto") CommonIconDto commonIconDto,
                                                      @Param(value = "page") Page<CommonIconPo> page);

    /**
     * 根据图标所属分区查询当前最大的排序号
     *
     * @param iconPartition 图标所属分区
     * @return 当前最大的排序号
     */
    int findMaxOrderNumByIconPartition(@Param("iconPartition") String iconPartition);

    /**
     * 查询通用图标数据
     *
     * @param commonIconDto 查询入参
     * @return 通用图标数据
     */
    FindCommonIconEntity findCommonIcon(@Param(value = "commonIconDto") CommonIconDto commonIconDto);
}
