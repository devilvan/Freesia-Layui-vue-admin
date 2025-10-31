package com.freesia.worldclock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freesia.constant.FlagConstant;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.util.UCopy;
import com.freesia.util.UEmpty;
import com.freesia.worldclock.dto.WorldClockCityDto;
import com.freesia.worldclock.mapper.WorldClockCityMapper;
import com.freesia.worldclock.po.WorldClockCityPo;
import com.freesia.worldclock.repository.WorldClockCityRepository;
import com.freesia.worldclock.service.WorldClockCityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 城市表 业务逻辑类
 * @date 2025-10-31
 */
@Service
@RequiredArgsConstructor
public class WorldClockCityServiceImpl extends ServiceImpl<WorldClockCityMapper, WorldClockCityPo> implements WorldClockCityService {
    private final WorldClockCityRepository worldClockCityRepository;

    @Override
    public WorldClockCityDto saveUpdate(WorldClockCityDto worldClockCityDto) {
        WorldClockCityPo worldClockCityPo = new WorldClockCityPo();
        UCopy.fullCopy(worldClockCityDto, worldClockCityPo);
        WorldClockCityDto resultDto = new WorldClockCityDto();
        UCopy.fullCopy(worldClockCityRepository.saveAndFlush(worldClockCityPo), resultDto);
        return resultDto;
    }

    @Override
    public List<WorldClockCityDto> saveUpdateBatch(List<WorldClockCityDto> list) {
        List<WorldClockCityPo> worldClockCityPoList = UCopy.fullCopyList(list, WorldClockCityPo.class);
        return UCopy.fullCopyList(worldClockCityRepository.saveAllAndFlush(worldClockCityPoList), WorldClockCityDto.class);
    }

    @Override
    public TableResult<WorldClockCityDto> findPageWorldClockCity(WorldClockCityDto worldClockCityDto, PageQuery pageQuery) {
        LambdaQueryWrapper<WorldClockCityPo> wrapper = new LambdaQueryWrapper<WorldClockCityPo>()
                .eq(WorldClockCityPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(worldClockCityDto.getId()), WorldClockCityPo::getId, worldClockCityDto.getId());
        Page<WorldClockCityPo> pagePo = page(pageQuery.build(), wrapper);
        return TableResult.build(UCopy.convertPagePo2Dto(pagePo, WorldClockCityDto.class));
    }

    @Override
    public WorldClockCityDto findWorldClockCity(WorldClockCityDto worldClockCityDto) {
        LambdaQueryWrapper<WorldClockCityPo> wrapper = new LambdaQueryWrapper<WorldClockCityPo>()
            .eq(WorldClockCityPo::getLogicDel, FlagConstant.DISABLED)
            .eq(UEmpty.isNotEmpty(worldClockCityDto.getId()), WorldClockCityPo::getId, worldClockCityDto.getId());
        return UCopy.copyPo2Dto(getOne(wrapper), WorldClockCityDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorldClockCity(List<Long> idList) {
        removeBatchByIds(idList);
    }

    /**
     * 为指定城市生成全年日出日落数据
     */
    public void generateYearlyDataForCity(int cityId, int year) {
        // 获取城市信息
        WorldClockCityPo city = getById(cityId);
        if (city == null) {
            throw new IllegalArgumentException("城市ID不存在: " + cityId);
        }
        // 删除该城市该年的现有数据
        deleteExistingData(cityId, year);

        // 生成全年数据
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<SunriseSunsetData> dataList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            SunriseSunsetCalculator.SunriseSunsetResult result =
                    SunriseSunsetCalculator.calculateSunriseSunset(
                            city.getLatitude(),
                            city.getLongitude(),
                            date,
                            city.getTimezone()
                    );

            SunriseSunsetData data = new SunriseSunsetData(
                    cityId, date, result.getSunrise(), result.getSunset()
            );

            dataList.add(data);

            // 每100条数据批量插入一次
            if (dataList.size() >= 100) {
                batchInsertData(dataList);
                dataList.clear();
            }
        }

        // 插入剩余数据
        if (!dataList.isEmpty()) {
            batchInsertData(dataList);
        }

        System.out.println("成功生成 " + year + " 年城市 " + city.getCityName() + " 的日出日落数据");
    }

    /**
     * 为所有城市生成全年日出日落数据
     */
    public void generateYearlyDataForAllCities(int year) throws SQLException {
        List<City> cities = getAllCities();

        for (City city : cities) {
            generateYearlyDataForCity(city.getId(), year);
        }

        System.out.println("成功生成 " + year + " 年所有城市的日出日落数据");
    }

    /**
     * 删除现有数据
     */
    private void deleteExistingData(Long cityId, int year) throws SQLException {
        String sql = "DELETE FROM sunrise_sunset_data WHERE city_id = ? AND YEAR(date) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cityId);
            stmt.setInt(2, year);
            stmt.executeUpdate();
        }
        worldClockCityRepository.deleteExistingData(cityId, year);
    }
}
