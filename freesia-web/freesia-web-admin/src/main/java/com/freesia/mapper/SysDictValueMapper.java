package com.freesia.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.po.SysDictValuePo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 字典值信息表 持久层
 * @date 2023-09-08
 */
@Mapper
public interface SysDictValueMapper extends BaseMapper<SysDictValuePo> {
    /**
     * 查询字典值分页数据
     *
     * @param page    分页参数
     * @param wrapper 构造的入参SQL
     * @return 字典值分页数据
     */
    Page<SysDictValuePo> findPageSysDictValue(@Param("page") Page<SysDictValuePo> page, @Param(Constants.WRAPPER) Wrapper<SysDictValuePo> wrapper);

    /**
     * 查询字典值列表数据
     *
     * @param wrapper 构造的入参SQL
     * @return 字典值列表数据
     */
    List<SysDictValuePo> findSysDictValueList(@Param(Constants.WRAPPER) Wrapper<SysDictValuePo> wrapper);

    /**
     * 根据字典键、字典值名称，查询数据
     *
     * @param distinctDictValueNameList 去重的字典值名称
     * @param dictKey                   字典键
     * @param keyId                     字典键ID
     * @return 结果集
     */
    List<SysDictValuePo> findDistinctDictValueNameList(@Param("distinctDictValueNameList") List<String> distinctDictValueNameList, @Param("dictKey") String dictKey, @Param("keyId") Long keyId);

    /**
     * 根据字典键ID查询最大的排序号
     *
     * @param keyId 字典键ID
     * @return 最大排序号
     */
    Integer findMaxOrderNumByKeyId(@Param("keyId") Long keyId);

    /**
     * 判断字典是否已经有默认值了，如果有则替换
     *
     * @param keyId 字典键ID
     * @return 默认值标识
     */
    List<SysDictValuePo> findDefaultFlagByKeyId(@Param("keyId") Long keyId);
}
