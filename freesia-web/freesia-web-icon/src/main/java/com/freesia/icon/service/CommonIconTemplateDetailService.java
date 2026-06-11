package com.freesia.icon.service;

import com.freesia.icon.dto.CommonIconTemplateDetailDto;
import com.freesia.icon.entity.FindCommonIconTemplateDetailEntity;
import com.freesia.icon.entity.FindTreeIconTreeTypeEntity;
import com.freesia.pojo.LaySelect;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

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
     * @param dto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    List<CommonIconTemplateDetailDto> saveUpdateBatch(CommonIconTemplateDetailDto dto);

    /**
     * 查询通用图标模板表信息
     *
     * @param commonIconTemplateDetailDto 查询条件
     * @param pageQuery                   分页条件
     * @return 分页信息
     */
    TableResult<CommonIconTemplateDetailDto> findPage(CommonIconTemplateDetailDto commonIconTemplateDetailDto, PageQuery pageQuery);

    /**
     * 条件查询通用图标模板表信息
     *
     * @param commonIconTemplateDetailDto 查询条件
     * @return 通用图标模板表信息
     */
    FindCommonIconTemplateDetailEntity findCommonIconTemplateDetail(CommonIconTemplateDetailDto commonIconTemplateDetailDto);

    /**
     * 删除通用图标模板表信息
     *
     * @param idList 主键
     */
    void deleteBatch(List<Long> idList);

    /**
     * 查询通用图标模板明细的节点数据
     *
     * @param commonIconTemplateDetailDto 查询入参
     * @return 通用图标模板明细的节点数据
     */
    List<FindTreeIconTreeTypeEntity> findTreeIconTreeType(CommonIconTemplateDetailDto commonIconTemplateDetailDto);

    /**
     * 查询自增排序号
     *
     * @param commonIconTemplateDetailDto 查询入参
     * @return 最大排序号
     */
    Integer findMaxOrderNum(CommonIconTemplateDetailDto commonIconTemplateDetailDto);

    /**
     * 查询自定义分组列表
     *
     * @param dto 查询入参
     * @return 结果集
     */
    List<LaySelect> findGrouping(CommonIconTemplateDetailDto dto);

    /**
     * 查询自定义分组Map
     *
     * @param dto 查询入参
     * @return 结果集
     */
    List<FindTreeIconTreeTypeEntity> findCustomIconTemplateDetail(CommonIconTemplateDetailDto dto);

    /**
     * 删除自定义分组
     *
     * @param commonIconTemplateDetailDto 删除条件
     */
    void deleteGrouping(CommonIconTemplateDetailDto commonIconTemplateDetailDto);

    /**
     * 查询缓存默认通用图标模板明细
     *
     * @return 结果集
     */
    List<CommonIconTemplateDetailDto> findCacheDefaultCommonIconDetail();

    /**
     * 查询通用图标模板明细是否存在
     *
     * @param headerId 通用图标模板表Id
     */
    Boolean findByHeaderIdExists(Long headerId);
}
