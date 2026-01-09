package com.freesia.service;

import com.freesia.dto.UrlConfigDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description URL配置信息表 业务逻辑接口
 * @date 2024-01-24
 */
public interface UrlConfigService {
    /**
     * 保存
     *
     * @param urlConfigDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    UrlConfigDto saveUpdate(UrlConfigDto urlConfigDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<UrlConfigDto> saveUpdateBatch(List<UrlConfigDto> list);

    /**
     * 查询URL配置分页信息
     *
     * @param urlConfigDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<UrlConfigDto> findPage(UrlConfigDto urlConfigDto, PageQuery pageQuery);

    /**
     * 条件查询URL配置信息
     *
     * @param urlConfigDto 查询条件
     * @return URL配置信息
     */
    UrlConfigDto findOne(UrlConfigDto urlConfigDto);

    /**
     * （缓存）根据配置标识查询URL配置分页信息
     *
     * @param code 配置标识
     * @return URL配置信息
     */
    UrlConfigDto findCacheUrlConfigByCode(String code);

    /**
     * 删除URL配置信息
     *
     * @param id   主键
     * @param code 配置标识
     */
    void deleteUrlConfig(Long id, String code);
}
