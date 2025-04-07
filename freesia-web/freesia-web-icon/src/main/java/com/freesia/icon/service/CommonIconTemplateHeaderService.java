package com.freesia.icon.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.icon.dto.CommonIconTemplateHeaderDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 通用图标模板头表 业务逻辑接口
 * @date 2025-04-07
 */
public interface CommonIconTemplateHeaderService {
    /**
     * 保存通用图标模板头表信息
     *
     * @param commonIconTemplateHeaderDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    CommonIconTemplateHeaderDto saveUpdate(CommonIconTemplateHeaderDto commonIconTemplateHeaderDto);

    /**
     * 批量保存通用图标模板头表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<CommonIconTemplateHeaderDto> saveUpdateBatch(List<CommonIconTemplateHeaderDto> list);

    /**
     * 查询通用图标模板头表信息
     *
     * @param commonIconTemplateHeaderDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<CommonIconTemplateHeaderDto> findPageCommonIconTemplateHeader(CommonIconTemplateHeaderDto commonIconTemplateHeaderDto, PageQuery pageQuery);

    /**
     * 条件查询通用图标模板头表信息
     *
     * @param commonIconTemplateHeaderDto 查询条件
     * @return 通用图标模板头表信息
     */
    CommonIconTemplateHeaderDto findCommonIconTemplateHeader(CommonIconTemplateHeaderDto commonIconTemplateHeaderDto);

    /**
     * 删除通用图标模板头表信息
     *
     * @param idList 主键
     */
    void deleteCommonIconTemplateHeader(List<Long> idList);
}
