package com.freesia.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.dto.CommonTodoDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 待办事项表 业务逻辑接口
 * @date 2026-01-04
 */
public interface CommonTodoService {
    /**
     * 保存待办事项表信息
     *
     * @param commonTodoDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    CommonTodoDto saveUpdate(CommonTodoDto commonTodoDto);

    /**
     * 批量保存待办事项表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<CommonTodoDto> saveUpdateBatch(List<CommonTodoDto> list);

    /**
     * 查询待办事项表信息
     *
     * @param commonTodoDto 查询条件
     * @param pageQuery     分页条件
     * @return 分页信息
     */
    TableResult<CommonTodoDto> findPage(CommonTodoDto commonTodoDto, PageQuery pageQuery);

    /**
     * 条件查询待办事项表信息
     *
     * @param commonTodoDto 查询条件
     * @return 待办事项表信息
     */
    CommonTodoDto findOne(CommonTodoDto commonTodoDto);

    /**
     * 条件查询待办事项表信息
     *
     * @param commonTodoDto 查询条件
     * @return 待办事项表信息
     */
    List<CommonTodoDto> findList(CommonTodoDto commonTodoDto);

    /**
     * 删除待办事项表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);
}
