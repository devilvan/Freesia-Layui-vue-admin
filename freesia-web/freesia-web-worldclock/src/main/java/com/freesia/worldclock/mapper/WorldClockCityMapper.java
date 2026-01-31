package com.freesia.worldclock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.worldclock.po.WorldClockCityPo;
import com.freesia.worldclock.dto.WorldClockCityDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 城市表 持久层
 * @date 2026-01-31
 */
@Mapper
public interface WorldClockCityMapper extends BaseMapper<WorldClockCityPo> {
    /**
     * 分页查询城市表信息
     *
     * @param worldClockCityDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    Page<WorldClockCityPo> findPageWorldClockCity(@Param(value = "dto") WorldClockCityDto worldClockCityDto, @Param("page") Page<WorldClockCityPo> page);

    /**
     * 查询城市表信息
     *
     * @param worldClockCityDto 查询条件
     * @return 分页信息
     */
    List<WorldClockCityDto> findListWorldClockCity(@Param(value = "dto") WorldClockCityDto worldClockCityDto);

    /**
     * 批量新增
     *
     * @param list 待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<WorldClockCityPo> list);

    /**
     * 批量更新
     *
     * @param list 待新增集合
     * @return 更新数量
     */
    int updateBatch(@Param(value = "list") List<WorldClockCityPo> list);

}
