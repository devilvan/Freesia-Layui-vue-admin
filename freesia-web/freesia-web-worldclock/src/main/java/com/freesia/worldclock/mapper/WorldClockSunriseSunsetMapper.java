package com.freesia.worldclock.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.worldclock.dto.WorldClockSunriseSunsetDto;
import com.freesia.worldclock.entity.FindCitySunriseSunsetEntity;
import com.freesia.worldclock.po.WorldClockSunriseSunsetPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 日出日落时间表 持久层
 * @date 2025-10-31
 */
@Mapper
public interface WorldClockSunriseSunsetMapper extends BaseMapper<WorldClockSunriseSunsetPo> {

}
