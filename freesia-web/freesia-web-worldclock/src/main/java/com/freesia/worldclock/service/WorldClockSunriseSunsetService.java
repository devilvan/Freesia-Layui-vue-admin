package com.freesia.worldclock.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.worldclock.dto.WorldClockSunriseSunsetDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 日出日落时间表 业务逻辑接口
 * @date 2025-10-31
 */
public interface WorldClockSunriseSunsetService {
    /**
     * 保存日出日落时间表信息
     *
     * @param worldClockSunriseSunsetDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    WorldClockSunriseSunsetDto saveUpdate(WorldClockSunriseSunsetDto worldClockSunriseSunsetDto);

    /**
     * 批量保存日出日落时间表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<WorldClockSunriseSunsetDto> saveUpdateBatch(List<WorldClockSunriseSunsetDto> list);

    /**
     * 查询日出日落时间表信息
     *
     * @param worldClockSunriseSunsetDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<WorldClockSunriseSunsetDto> findPageWorldClockSunriseSunset(WorldClockSunriseSunsetDto worldClockSunriseSunsetDto, PageQuery pageQuery);

    /**
     * 条件查询日出日落时间表信息
     *
     * @param worldClockSunriseSunsetDto 查询条件
     * @return 日出日落时间表信息
     */
    WorldClockSunriseSunsetDto findWorldClockSunriseSunset(WorldClockSunriseSunsetDto worldClockSunriseSunsetDto);

    /**
     * 删除日出日落时间表信息
     *
     * @param idList 主键
     */
    void deleteWorldClockSunriseSunset(List<Long> idList);
}
