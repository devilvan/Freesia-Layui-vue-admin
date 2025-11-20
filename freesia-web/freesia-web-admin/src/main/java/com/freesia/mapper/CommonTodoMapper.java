package com.freesia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.po.CommonTodoPo;
import com.freesia.dto.CommonTodoDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 待办事项表 持久层
 * @date 2025-11-20
 */
@Mapper
public interface CommonTodoMapper extends BaseMapper<CommonTodoPo> {
    /**
    * 分页查询待办事项表信息
    *
    * @param commonTodoDto 查询条件
    * @param pageQuery    分页条件
    * @return 分页信息
    */
    Page<CommonTodoPo> findPageCommonTodo(@Param(value = "dto") CommonTodoDto commonTodoDto, @Param("page") Page<CommonTodoPo> page);

    /**
    * 查询待办事项表信息
    *
    * @param commonTodoDto 查询条件
    * @return 分页信息
    */
    List<CommonTodoDto> findListCommonTodo(@Param(value = "dto") CommonTodoDto commonTodoDto);

    /**
     * 批量新增
     *
     * @param list    待新增集合
     * @return 新增数量
     */
    int insertBatch(@Param(value = "list") List<CommonTodoPo> list);

    /**
    * 批量更新
    *
    * @param list    待新增集合
    * @return 更新数量
    */
    int updateBatch(@Param(value = "list") List<CommonTodoPo> list);

}
