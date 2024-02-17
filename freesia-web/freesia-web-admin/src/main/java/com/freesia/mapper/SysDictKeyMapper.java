package com.freesia.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.entity.FindPageSysDictKeyEntity;
import com.freesia.po.SysDictKeyPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 字典键信息表 持久层
 * @date 2023-09-08
 */
@Mapper
public interface SysDictKeyMapper extends BaseMapper<SysDictKeyPo> {
    /**
     * 查询字典数据的分页信息
     *
     * @param page    分页参数
     * @param wrapper 构造的条件SQL
     * @return 分页对象
     */
    Page<FindPageSysDictKeyEntity> findPageSysDictList(@Param("page") Page<SysDictKeyPo> page, @Param(Constants.WRAPPER) Wrapper<SysDictKeyPo> wrapper);

    /**
     * 查询字典键列表
     *
     * @param wrapper 条件SQL
     * @return 字典键列表
     */
    List<SysDictKeyPo> findSysDictKeyList(@Param(Constants.WRAPPER) Wrapper<SysDictKeyPo> wrapper);
}
