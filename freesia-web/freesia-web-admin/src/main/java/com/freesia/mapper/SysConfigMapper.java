package com.freesia.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.po.SysConfigPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Evad.Wu
 * @Description 全局配置信息表 持久层
 * @date 2023-08-12
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfigPo> {
    /**
     * 获取参数配置分页
     *
     * @param page    分页参数
     * @param wrapper 查询参数构建的SQL
     * @return 参数配置分页对象
     */
    Page<SysConfigPo> findPageSysConfig(@Param("page") Page<SysConfigPo> page, @Param(Constants.WRAPPER) Wrapper<SysConfigPo> wrapper);
}
