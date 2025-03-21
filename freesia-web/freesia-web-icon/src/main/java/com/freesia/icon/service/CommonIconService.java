package com.freesia.icon.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.icon.dto.CommonIconDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标表 业务逻辑接口
 * @date 2025-03-21
 */
public interface CommonIconService {
    /**
     * 保存通用图标表信息
     *
     * @param commonIconDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    CommonIconDto saveUpdate(CommonIconDto commonIconDto);

    /**
     * 批量保存通用图标表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<CommonIconDto> saveUpdateBatch(List<CommonIconDto> list);

    /**
     * 查询通用图标表信息
     *
     * @param commonIconDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<CommonIconDto> findPageCommonIcon(CommonIconDto commonIconDto, PageQuery pageQuery);

    /**
     * 条件查询通用图标表信息
     *
     * @param commonIconDto 查询条件
     * @return 通用图标表信息
     */
    CommonIconDto findCommonIcon(CommonIconDto commonIconDto);

    /**
     * 删除通用图标表信息
     *
     * @param idList 主键
     */
    void deleteCommonIcon(List<Long> idList);
}
