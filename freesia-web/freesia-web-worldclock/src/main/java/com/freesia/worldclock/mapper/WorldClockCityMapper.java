package com.freesia.worldclock.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.worldclock.dto.FindCitySunriseSunsetReqDto;
import com.freesia.worldclock.dto.WorldClockCityDto;
import com.freesia.worldclock.entity.FindCitySunriseSunsetEntity;
import com.freesia.worldclock.po.WorldClockCityPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 城市表 持久层
 * @date 2025-10-31
 */
@Mapper
public interface WorldClockCityMapper extends BaseMapper<WorldClockCityPo> {

    /**
     * 条件查询
     *
     * @param worldClockCityDto 查询条件
     * @return 结果集
     */
    List<WorldClockCityPo> findListWorldClockCity(@Param(value = "dto") WorldClockCityDto worldClockCityDto);

    /**
     * 条件查询城市日出日落时间表信息
     *
     * @param findCitySunriseSunsetReqDto 查询条件
     * @return 结果集
     */
    List<FindCitySunriseSunsetEntity> findCitySunriseSunset(@Param(value = "dto") FindCitySunriseSunsetReqDto findCitySunriseSunsetReqDto);
}
