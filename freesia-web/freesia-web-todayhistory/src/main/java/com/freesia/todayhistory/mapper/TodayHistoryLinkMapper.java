package com.freesia.todayhistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.todayhistory.po.TodayHistoryLinkPo;
import com.freesia.todayhistory.dto.TodayHistoryLinkDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 历史上的今天-链接表 持久层
 * @date 2026-09-04
 */
@Mapper
public interface TodayHistoryLinkMapper extends BaseMapper<TodayHistoryLinkPo> {
    /**
     * 分页查询历史上的今天-链接表信息
     *
     * @param todayHistoryLinkDto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<TodayHistoryLinkPo> findPage(@Param(value = "dto") TodayHistoryLinkDto todayHistoryLinkDto, @Param("page") Page<TodayHistoryLinkPo> page);

    /**
     * 查询历史上的今天-链接表信息
     *
     * @param todayHistoryLinkDto 查询条件
     * @return 分页信息
     */
    List<TodayHistoryLinkPo> findList(@Param(value = "dto") TodayHistoryLinkDto todayHistoryLinkDto);

    /**
     * 查询历史上的今天-链接表信息
     *
     * @param todayHistoryLinkDto 查询条件
     * @return 分页信息
     */
    TodayHistoryLinkPo findOne(@Param(value = "dto") TodayHistoryLinkDto todayHistoryLinkDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<TodayHistoryLinkPo> list);
}
