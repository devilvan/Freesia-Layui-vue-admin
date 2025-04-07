package com.freesia.icon.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.icon.dto.CommonIconTemplateDetailDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板表 业务逻辑接口
 * @date 2025-04-07
 */
public interface CommonIconTemplateDetailService {
    /**
     * 保存通用图标模板表信息
     *
     * @param commonIconTemplateDetailDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    CommonIconTemplateDetailDto saveUpdate(CommonIconTemplateDetailDto commonIconTemplateDetailDto);

    /**
     * 批量保存通用图标模板表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<CommonIconTemplateDetailDto> saveUpdateBatch(List<CommonIconTemplateDetailDto> list);

    /**
     * 查询通用图标模板表信息
     *
     * @param commonIconTemplateDetailDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<CommonIconTemplateDetailDto> findPageCommonIconTemplateDetail(CommonIconTemplateDetailDto commonIconTemplateDetailDto, PageQuery pageQuery);

    /**
     * 条件查询通用图标模板表信息
     *
     * @param commonIconTemplateDetailDto 查询条件
     * @return 通用图标模板表信息
     */
    CommonIconTemplateDetailDto findCommonIconTemplateDetail(CommonIconTemplateDetailDto commonIconTemplateDetailDto);

    /**
     * 删除通用图标模板表信息
     *
     * @param idList 主键
     */
    void deleteCommonIconTemplateDetail(List<Long> idList);
}
