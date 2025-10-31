package com.freesia.worldclock.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.worldclock.po.WorldClockCityPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Evad.Wu
 * @Description 城市表 持久层
 * @date 2025-10-31
 */
@Mapper
public interface WorldClockCityMapper extends BaseMapper<WorldClockCityPo> {

}
