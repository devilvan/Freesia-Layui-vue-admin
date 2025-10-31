package com.freesia.worldclock.service;

import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.worldclock.dto.WorldClockCityDto;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 城市表 业务逻辑接口
 * @date 2025-10-31
 */
public interface WorldClockCityService {
    /**
     * 保存城市表信息
     *
     * @param worldClockCityDto 控制层处理后的数据传输对象
     * @return 保存回调对象
     */
    WorldClockCityDto saveUpdate(WorldClockCityDto worldClockCityDto);

    /**
     * 批量保存城市表信息
     *
     * @param list 控制层处理后的数据传输对象集合
     * @return 保存回调对象
     */
    List<WorldClockCityDto> saveUpdateBatch(List<WorldClockCityDto> list);

    /**
     * 查询城市表信息
     *
     * @param worldClockCityDto 查询条件
     * @param pageQuery    分页条件
     * @return 分页信息
     */
    TableResult<WorldClockCityDto> findPageWorldClockCity(WorldClockCityDto worldClockCityDto, PageQuery pageQuery);

    /**
     * 条件查询城市表信息
     *
     * @param worldClockCityDto 查询条件
     * @return 城市表信息
     */
    WorldClockCityDto findWorldClockCity(WorldClockCityDto worldClockCityDto);

    /**
     * 删除城市表信息
     *
     * @param idList 主键
     */
    void deleteWorldClockCity(List<Long> idList);
}
