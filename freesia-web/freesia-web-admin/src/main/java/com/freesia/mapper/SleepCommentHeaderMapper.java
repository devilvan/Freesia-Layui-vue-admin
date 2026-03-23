package com.freesia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.po.SleepCommentHeaderPo;
import com.freesia.dto.SleepCommentHeaderDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 睡眠产品评论 持久层
 * @date 2026-03-23
 */
@Mapper
public interface SleepCommentHeaderMapper extends BaseMapper<SleepCommentHeaderPo> {
    /**
     * 分页查询睡眠产品评论信息
     *
     * @param sleepCommentHeaderDto 查询条件
     * @param page    分页条件
     * @return 分页信息
     */
    Page<SleepCommentHeaderPo> findPage(@Param(value = "dto") SleepCommentHeaderDto sleepCommentHeaderDto, @Param("page") Page<SleepCommentHeaderPo> page);

    /**
     * 查询睡眠产品评论信息
     *
     * @param sleepCommentHeaderDto 查询条件
     * @return 分页信息
     */
    List<SleepCommentHeaderDto> findList(@Param(value = "dto") SleepCommentHeaderDto sleepCommentHeaderDto);

    /**
     * 查询睡眠产品评论信息
     *
     * @param sleepCommentHeaderDto 查询条件
     * @return 分页信息
     */
    SleepCommentHeaderPo findOne(@Param(value = "dto") SleepCommentHeaderDto sleepCommentHeaderDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<SleepCommentHeaderPo> list);
}
