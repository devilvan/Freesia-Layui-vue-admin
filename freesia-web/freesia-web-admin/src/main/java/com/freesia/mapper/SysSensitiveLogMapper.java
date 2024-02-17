package com.freesia.mapper;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.po.SysSensitiveLogPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Evad.Wu
 * @Description 敏感操作信息表 持久层
 * @date 2023-08-13
 */
@Mapper
public interface SysSensitiveLogMapper extends BaseMapper<SysSensitiveLogPo> {
    /**
     * 查询登录日志分页数据
     *
     * @param page    分页参数
     * @param wrapper 查询条件
     * @return 分页返回
     */
    Page<SysSensitiveLogPo> findPageLoginLog(@Param("page") Page<SysSensitiveLogPo> page, @Param(Constants.WRAPPER) Wrapper<SysSensitiveLogPo> wrapper);

    /**
     * 查询操作日志分页数据
     *
     * @param page    分页参数
     * @param wrapper 查询条件
     * @return 分页返回
     */
    Page<SysSensitiveLogPo> findPageOptionLog(@Param("page") Page<SysSensitiveLogPo> page, @Param(Constants.WRAPPER) Wrapper<SysSensitiveLogPo> wrapper);

}
