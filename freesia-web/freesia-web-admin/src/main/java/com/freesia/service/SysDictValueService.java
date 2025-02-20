package com.freesia.service;

import com.freesia.dto.SysDictValueDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 字典值信息表 业务逻辑接口
 * @date 2023-09-08
 */
public interface SysDictValueService {
    /**
     * 保存
     *
     * @param sysDictValueDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    SysDictValueDto saveUpdate(SysDictValueDto sysDictValueDto);

    /**
     * 批量保存
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<SysDictValueDto> saveUpdateBatch(List<SysDictValueDto> list);

    /**
     * 查询字典值分页数据
     *
     * @param sysDictValueDto 查询参数
     * @param pageQuery       分页参数
     * @return 分页字典值数据
     */
    TableResult<SysDictValueDto> findPageSysDictValue(SysDictValueDto sysDictValueDto, PageQuery pageQuery);

    /**
     * 查询字典值列表数据
     *
     * @param sysDictValueDto 查询参数
     * @return 字典值列表数据
     */
    List<SysDictValueDto> findSysDictValueList(SysDictValueDto sysDictValueDto);

    /**
     * （缓存）查询字典值列表数据
     *
     * @param dictKey 字典键
     * @return 字典值列表数据
     */
    List<SysDictValueDto> findCacheSysDictValueList(String dictKey);

    /**
     * 应用启动初始化数据字典
     */
    void loadSysDictValue();

    /**
     * 保存字典值
     *
     * @param sysDictValueDto 字典值数据
     * @return 保存后的字典值
     */
    SysDictValueDto saveSysDictValue(SysDictValueDto sysDictValueDto);

    /**
     * 刷新缓存
     *
     * @param dictKey 字典键
     */
    void flushCacheSysDictValue(String dictKey);

    /**
     * 删除字典键
     *
     * @param idList 字典键ID集合
     */
    void deleteSysDictValueList(List<Long> idList);

    /**
     * 启用/禁用字典键
     *
     * @param idList 字典键ID集合
     */
    void enableSysDictValueList(List<Long> idList);

    /**
     * 根据字典键、字典值名称，查询数据
     *
     * @param distinctDictValueNameList 去重的字典值名称
     * @param dictKey                   字典键
     * @param keyId                     字典键ID
     * @return 结果集
     */
    List<SysDictValueDto> findDistinctDictValueNameList(List<String> distinctDictValueNameList, String dictKey, Long keyId);

    /**
     * 根据字典键ID查询最大的排序号
     *
     * @param keyId 字典键ID
     * @return 最大排序号
     */
    Integer findMaxOrderNumByKeyId(Long keyId);
}
