package com.freesia.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.po.SysTenantPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Evad.Wu
 * @Description 租户信息表 持久层
 * @date 2024-01-31
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenantPo> {

}
